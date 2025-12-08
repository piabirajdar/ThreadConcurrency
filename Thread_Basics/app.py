# backend/app.py
from fastapi import FastAPI, WebSocket
from pydantic import BaseModel
import asyncio
import openai

openai.api_key = "YOUR_OPENAI_API_KEY"

app = FastAPI()

# Mock APIs

class MockFileSystem:
    def __init__(self):
        self.files = {"todo.txt": "Buy milk\nCall Alice"}

    def read_file(self, filename):
        return self.files.get(filename, "")

    def write_file(self, filename, content):
        self.files[filename] = content
        return "File written."

class MockCalendar:
    def __init__(self):
        self.events = []

    def add_event(self, title, date):
        self.events.append({"title": title, "date": date})
        return f"Event '{title}' added on {date}."

file_system = MockFileSystem()
calendar = MockCalendar()

# Agent Logic (very simple loop)

class Task(BaseModel):
    text: str

active_tasks = {}

async def agent_step(task_text, history):
    # Simplified prompt to GPT to decide next action
    prompt = f"""
You are an agent with access to the following tools:
- FileSystem: read_file(filename), write_file(filename, content)
- Calendar: add_event(title, date)

Current conversation history:
{history}

Your task is: {task_text}

Provide your next action in JSON format with fields:
{{"action": "<tool>.<method>", "args": [arguments], "reason": "<why>" }}

If done, respond with {{"action": "done"}}.
"""
    response = openai.ChatCompletion.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}],
        temperature=0.2,
        max_tokens=150,
    )
    return response.choices[0].message.content

@app.websocket("/ws/{task_id}")
async def websocket_agent(ws: WebSocket, task_id: str):
    await ws.accept()
    history = ""
    done = False

    while not done:
        data = await ws.receive_json()
        if data.get("command") == "start":
            task_text = data["task"]
            history += f"User: {task_text}\n"
            while True:
                action_json = await agent_step(task_text, history)
                # Parse action_json safely
                try:
                    import json
                    action_data = json.loads(action_json)
                except Exception:
                    await ws.send_json({"error": "Failed to parse agent action."})
                    break

                reason = action_data.get("reason", "")
                action = action_data.get("action", "")
                args = action_data.get("args", [])

                if action == "done":
                    await ws.send_json({"action": "done", "reason": reason})
                    done = True
                    break

                # Execute mocked action
                result = None
                if action == "FileSystem.read_file":
                    result = file_system.read_file(*args)
                elif action == "FileSystem.write_file":
                    result = file_system.write_file(*args)
                elif action == "Calendar.add_event":
                    result = calendar.add_event(*args)
                else:
                    result = "Unknown action."

                # Update history for next step
                history += f"Agent: action={action}, args={args}, reason={reason}, result={result}\n"

                # Send action & result back to frontend
                await ws.send_json({
                    "action": action,
                    "args": args,
                    "reason": reason,
                    "result": result,
                })

                await asyncio.sleep(1)  # slow down loop for demo

        elif data.get("command") == "stop":
            await ws.close()
            break

