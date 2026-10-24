import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // environment variables
  define: {
    'process.env': {
      VITE_API_BASE_URL: JSON.stringify(process.env.VITE_API_BASE_URL || 'http://a0885fdc45e1541659d8aac9c3a5d10c-1659669634.ap-southeast-2.elb.amazonaws.com:8081')
    }
  },
})
