let ws;

document.querySelector("#connect")?.addEventListener("click", (e) => {
    connect();
});

document.querySelector("#disconnect")?.addEventListener("click", (e) => {
    disconnect();
});

document.querySelector("#sendBtn")?.addEventListener("click", (e) => {
    const msg = document.querySelector("#sendMsg");
    sendMessage(msg?.value);
});

function connect() {
    ws = new WebSocket("ws://localhost:8081");

    ws.onopen = () => {
        console.log("[open connection]");
    };

    ws.onmessage = (event) => {
        console.log(`[Message from server]: ${event.data}`);
        const messages = document.querySelector("#messages");
        messages.innerHTML += `<div>${event.data}</div>`;
    };

    ws.onclose = () => {
        console.log("[close connection]");
    };
}

function sendMessage(msg) {
    console.log('[sendMessage] msg:', msg);
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(msg);
    } else {
        console.log('WebSocket not ready.');
    }
}

function disconnect() {
    if (ws) {
        ws.close();
    }
}