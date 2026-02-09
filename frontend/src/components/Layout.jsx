import { Outlet, Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { clearSession, getStoredUser, isLoggedIn } from "../auth";
import { me } from "../api";

export default function Layout() {
  const navigate = useNavigate();
  const [user, setUser] = useState(() => getStoredUser());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function bootstrap() {
      if (!isLoggedIn()) {
        setLoading(false);
        return;
      }
      try {
        const u = await me();
        setUser(u);
        localStorage.setItem("user", JSON.stringify(u));
      } catch (e) {
        // token invalid/expired -> logout
        clearSession();
        setUser(null);
      } finally {
        setLoading(false);
      }
    }
    bootstrap();
  }, []);

  const onLogout = () => {
    clearSession();
    setUser(null);
    navigate("/");
  };

  return (
    <div style={{ maxWidth: 960, margin: "0 auto", padding: 16 }}>
      <header style={{ display: "flex", gap: 16, alignItems: "center", marginBottom: 16 }}>
        <Link to="/" style={{ fontWeight: 700, textDecoration: "none" }}>
          MovieRama
        </Link>

        <div style={{ marginLeft: "auto", display: "flex", gap: 12, alignItems: "center" }}>
          {user ? (
            <>
              <span>Welcome back {user.username}!</span>
              <Link to="/movies/new">New Movie</Link>
              <button onClick={onLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login">Log in</Link>
              <Link to="/signup">Sign up</Link>
            </>
          )}
        </div>
      </header>

      {loading ? <p>Loading...</p> : <Outlet />}
    </div>
  );
}
