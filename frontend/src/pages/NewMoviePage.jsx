import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../api";

export default function NewMoviePage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await apiFetch("/api/movies", {
        method: "POST",
        auth: true,
        body: { title, description },
      });
      navigate("/");
    } catch (err) {
      setError(err.message || "Failed to create movie");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ maxWidth: 640 }}>
      <h2 style={{ marginTop: 0 }}>New Movie</h2>

      {error && (
        <div style={{ border: "1px solid #f0c", padding: 12, borderRadius: 8, marginBottom: 12 }}>
          <p style={{ margin: 0, color: "crimson" }}>{error}</p>
        </div>
      )}

      <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
        <label style={{ display: "grid", gap: 6 }}>
          <span>Title</span>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            maxLength={200}
            required
          />
        </label>

        <label style={{ display: "grid", gap: 6 }}>
          <span>Description</span>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={6}
            maxLength={2000}
            required
          />
        </label>

        <div style={{ display: "flex", gap: 8 }}>
          <button type="submit" disabled={loading}>
            {loading ? "Saving..." : "Create"}
          </button>
          <button type="button" onClick={() => navigate("/")} disabled={loading}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
  