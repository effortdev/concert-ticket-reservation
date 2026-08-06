import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage.jsx'
import QueuePage from './pages/QueuePage.jsx'
import SeatSelectPage from './pages/SeatSelectPage.jsx'
import ReservationResultPage from './pages/ReservationResultPage.jsx'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/queue/:eventId" element={<QueuePage />} />
        <Route path="/events/:eventId/seats" element={<SeatSelectPage />} />
        <Route path="/reservations/:id/result" element={<ReservationResultPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
