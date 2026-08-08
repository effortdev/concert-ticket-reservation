/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        bg: '#0B0D14',
        surface: '#14171F',
        'surface-hover': '#1B1F2A',
        border: '#262B38',
        accent: '#7C5CFF',
        'accent-dim': '#4C3B99',
        amber: '#FFB020',
        emerald: '#34D399',
        coral: '#F8717A',
        'text-muted': '#8B90A0',
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}