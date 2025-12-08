import uuid
import asyncio
from collections import Counter
from asyncio import Queue
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI()

queue = Queue()
job_store = {}   # job_id -> {"status": ..., "result": ...}

# ------------------------------
# Request Schema
# ------------------------------

class Message(BaseModel):
    speaker: str
    text: str

class AnalyzeRequest(BaseModel):
    transcripts: list[Message]


# ------------------------------
# Pure data-processing function
# ------------------------------

def summarize_transcripts(messages):
    speaker_freq = Counter(m.speaker for m in messages)
    return {
        "total_messages": len(messages),
        "speaker_freq": dict(speaker_freq)
    }


def count_patient_words(messages):
    return sum(len(m.text.split()) for m in messages if m.speaker == "Patient")


# ------------------------------
# Job system
# ------------------------------

async def enqueue_job(payload):
    job_id = str(uuid.uuid4())
    job_store[job_id] = {"status": "pending"}
    await queue.put((job_id, payload))
    return job_id


async def worker_loop():
    while True:
        job_id, messages = await queue.get()

        try:
            total_words = count_patient_words(messages)
            job_store[job_id] = {
                "status": "completed",
                "result": {"patient_words": total_words}
            }
        except Exception:
            job_store[job_id] = {"status": "failed"}

        queue.task_done()


# Start worker
asyncio.create_task(worker_loop())


# ------------------------------
# API endpoints
# ------------------------------

@app.post("/analyze")
async def analyze(data: AnalyzeRequest):
    summary = summarize_transcripts(data.transcripts)
    job_id = await enqueue_job(data.transcripts)
    summary["job_id"] = job_id
    return summary


@app.get("/status/{job_id}")
async def status(job_id: str):
    if job_id not in job_store:
        raise HTTPException(status_code=404, detail="Job not found")
    return job_store[job_id]
