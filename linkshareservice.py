from __future__ import annotations
import asyncio
import socket

link = input("enter link to share> ")
payload = f"""
HTTP/1.1 200 yaye
Server: linkshareservice
Date: none of yo buisness
Content-Length: {len(link)}
Content-Type: text/plain
Cache-Control: no-store
{link}
""".strip().encode("utf-8")

async def callback(reader, writer):
	writer.write(payload)
	await writer.drain()
	writer.close()
	await writer.wait_closed()

async def start():
#	print(f"starting server on {socket.gethostbyname(socket.gethostname())}:25565")
	serv = await asyncio.start_server(callback, host="0.0.0.0", port=8080)
	
	async with serv:
		await serv.serve_forever()

asyncio.run(start())
