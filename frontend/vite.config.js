import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In development the browser talks to Vite (port 5173) and Vite forwards /api to Spring Boot,
// so there are no CORS problems. Change the target with VITE_API_PROXY if the backend runs elsewhere.
const backend = process.env.VITE_API_PROXY || 'http://localhost:8080';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: backend, changeOrigin: true },
    },
  },
});
