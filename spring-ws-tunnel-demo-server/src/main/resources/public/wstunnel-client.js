import { LitElement, html } from 'lit-element';

class SimpleGreeting extends LitElement {
  static get properties() {
    return { name: { type: String } };
  }

  constructor() {
    super();
    this.name = 'World';
  }

  render() {
    // Create WebSocket connection.
    const socket = new WebSocket('ws://localhost:8080/tunnel-connector');

    // Connection opened
    socket.addEventListener('open', function (event) {
        this.name = "connected";
    });

    return html`<p>Hello, ${this.name}!</p>`;
  }
}

customElements.define('simple-greeting', SimpleGreeting);
