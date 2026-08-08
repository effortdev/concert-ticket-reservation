import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage.jsx'
import EventListPage from './pages/EventListPage.jsx'
import QueuePage from './pages/QueuePage.jsx'
import SeatSelectPage from './pages/SeatSelectPage.jsx'
import ReservationResultPage from './pages/ReservationResultPage.jsx'
import OAuthCallbackPage from './pages/OAuthCallbackPage.jsx'
import AdminEventsPage from './pages/AdminEventsPage.jsx'
import AdminDashboardPage from './pages/AdminDashboardPage.jsx'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/oauth/callback" element={<OAuthCallbackPage />} />
                <Route path="/events" element={<EventListPage />} />
                <Route path="/queue/:eventId" element={<QueuePage />} />
                <Route path="/events/:eventId/seats" element={<SeatSelectPage />} />
                <Route path="/reservations/:id/result" element={<ReservationResultPage />} />
                <Route path="/admin/events" element={<AdminEventsPage />} />
                <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App