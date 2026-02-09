import { useEffect, useMemo, useState } from "react";

function buildQuery(params) {
  const q = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== null && v !== undefined && v !== "") q.set(k, String(v));
  });
  const s = q.toString();
  return s ? `?${s}` : "";
}

export default function HomePage() {
  const [sort, setSort] = useState("DATE"); // DATE | LIKES | HATES
  const [userId, setUserId] = useState(null);

  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadMovies(nextSort = sort, nextUserId = userId) {
    setLoading(true);
    setError("");
    try {
      const qs = buildQuery({ sort: nextSort, userId: nextUserId ?? "" });
      const res = await fetch(`/api/movies${qs}`);
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Failed to load movies (${res.status})`);
      }
      const data = await res.json();
      // Περιμένουμε array. Αν γυρίσεις {items:[...]} στο backend, άλλαξε εδώ σε data.items.
      setMovies(Array.isArray(data) ? data : (data?.items ?? []));
    } catch (e) {
      setMovies([]);
      setError(e.message || "Failed to load movies");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadMovies();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sort, userId]);

  const title = useMemo(() => {
    if (userId) return `Movies by user #${userId}`;
    return "Movies";
  }, [userId]);

  return (
    <div style={{ display: "grid", gap: 12 }}>
      {/* Sort / Filter bar */}
      <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
        <strong>{title}</strong>

        <span style={{ marginLeft: 12 }}>Sort by:</span>
        <button onClick={() => setSort("LIKES")} disabled={sort === "LIKES"}>
          Likes
        </button>
        <button onClick={() => setSort("HATES")} disabled={sort === "HATES"}>
          Hates
        </button>
        <button onClick={() => setSort("DATE")} disabled={sort === "DATE"}>
          Date
        </button>

        {userId && (
          <button style={{ marginLeft: "auto" }} onClick={() => setUserId(null)}>
            Clear user filter
          </button>
        )}
      </div>

      {/* States */}
      {loading && <p style={{ margin: 0 }}>Loading...</p>}

      {!loading && error && (
        <div style={{ border: "1px solid #f0c", padding: 12, borderRadius: 8 }}>
          <p style={{ margin: 0, color: "crimson" }}>
            {error}
          </p>
          <p style={{ margin: "8px 0 0 0", fontSize: 12 }}>
            (If you haven’t implemented <code>/api/movies</code> yet, this is expected.)
          </p>
          <button style={{ marginTop: 8 }} onClick={() => loadMovies()}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && movies.length === 0 && (
        <p style={{ margin: 0 }}>No movies yet.</p>
      )}

      {/* Movie cards */}
      {!loading && !error && movies.length > 0 && (
        <div style={{ display: "grid", gap: 12 }}>
          {movies.map((m) => (
            <article
              key={m.id}
              style={{
                border: "1px solid #ddd",
                borderRadius: 8,
                padding: 12,
                display: "grid",
                gap: 8,
              }}
            >
              <h3 style={{ margin: 0 }}>{m.title}</h3>

              <p style={{ margin: 0 }}>{m.description}</p>

              <div style={{ fontSize: 13 }}>
                Posted by{" "}
                <button
                  onClick={() => setUserId(m.submittedBy?.id)}
                  disabled={!m.submittedBy?.id}
                  style={{
                    border: "none",
                    background: "transparent",
                    padding: 0,
                    color: "blue",
                    cursor: m.submittedBy?.id ? "pointer" : "default",
                    textDecoration: m.submittedBy?.id ? "underline" : "none",
                  }}
                  title="Filter by user"
                >
                  {m.submittedBy?.username ?? "unknown"}
                </button>{" "}
                on {m.createdAt ? new Date(m.createdAt).toLocaleString() : ""}
              </div>

              <footer style={{ display: "flex", gap: 12, alignItems: "center" }}>
                <span>
                  <strong>{m.likesCount ?? 0}</strong> likes |{" "}
                  <strong>{m.hatesCount ?? 0}</strong> hates
                </span>

                {/* Voting UI θα μπει αφού υλοποιήσουμε votes API */}
              </footer>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
