import { useEffect, useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { apiFetch } from "../api";

function buildQuery(params) {
  const q = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== null && v !== undefined && v !== "") q.set(k, String(v));
  });
  const s = q.toString();
  return s ? `?${s}` : "";
}

export default function HomePage() {
  const { user } = useOutletContext(); // provided by Layout via <Outlet context={{ user }} />

  const [sort, setSort] = useState("DATE"); // DATE | LIKES | HATES
  const [submittedBy, setSubmittedBy] = useState(null); // username

  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadMovies(nextSort = sort, nextSubmittedBy = submittedBy) {
    setLoading(true);
    setError("");
    try {
      const qs = buildQuery({ sort: nextSort, submittedBy: nextSubmittedBy ?? "" });
      // If logged in, include token so backend can return myVote/canVote correctly
      const data = await apiFetch(`/api/movies${qs}`, { auth: !!user });

      // Expect an array. If backend returns {items:[...]}, keep the fallback.
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
  }, [sort, submittedBy, user]);

  const title = useMemo(() => {
    if (submittedBy) return `Movies by ${submittedBy}`;
    return "Movies";
  }, [submittedBy]);

  async function onVote(movie, clickedType) {
    if (!user) return;
    if (!movie?.canVote) return;

    const nextType = movie.myVote === clickedType ? null : clickedType;

    try {
      const updated = await apiFetch(`/api/movies/${movie.id}/vote`, {
        method: "PUT",
        auth: true,
        body: { type: nextType },
      });
      setMovies((prev) => prev.map((m) => (m.id === updated.id ? updated : m)));
    } catch (e) {
      alert(e.message || "Vote failed");
    }
  }

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

        {submittedBy && (
          <button style={{ marginLeft: "auto" }} onClick={() => setSubmittedBy(null)}>
            Clear user filter
          </button>
        )}
      </div>

      {/* States */}
      {loading && <p style={{ margin: 0 }}>Loading...</p>}

      {!loading && error && (
        <div style={{ border: "1px solid #f0c", padding: 12, borderRadius: 8 }}>
          <p style={{ margin: 0, color: "crimson" }}>{error}</p>
          <button style={{ marginTop: 8 }} onClick={() => loadMovies()}>
            Retry
          </button>
        </div>
      )}

      {!loading && !error && movies.length === 0 && <p style={{ margin: 0 }}>No movies yet.</p>}

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
                  onClick={() => setSubmittedBy(m.submittedBy?.username)}
                  disabled={!m.submittedBy?.username}
                  style={{
                    border: "none",
                    background: "transparent",
                    padding: 0,
                    color: "blue",
                    cursor: m.submittedBy?.username ? "pointer" : "default",
                    textDecoration: m.submittedBy?.username ? "underline" : "none",
                  }}
                  title="Filter by user"
                >
                  {m.submittedBy?.username ?? "unknown"}
                </button>{" "}
                on {m.createdAt ? new Date(m.createdAt).toLocaleString() : ""}
              </div>

              <footer style={{ display: "flex", gap: 12, alignItems: "center" }}>
                <span>
                  <strong>{m.likesCount ?? 0}</strong> likes | <strong>{m.hatesCount ?? 0}</strong> hates
                </span>

                {!!user && (
                  <div style={{ marginLeft: "auto", display: "flex", gap: 8, alignItems: "center" }}>
                    {m.canVote ? (
                      <>
                        <button onClick={() => onVote(m, "LIKE")}>
                          {m.myVote === "LIKE" ? "Unlike" : "Like"}
                        </button>
                        <button onClick={() => onVote(m, "HATE")}>
                          {m.myVote === "HATE" ? "Unhate" : "Hate"}
                        </button>
                        {m.myVote && (
                          <span style={{ fontSize: 12 }}>
                            You {m.myVote === "LIKE" ? "like" : "hate"} this movie.
                          </span>
                        )}
                      </>
                    ) : (
                      <span style={{ fontSize: 12, opacity: 0.75 }}>You can’t vote on your own movie.</span>
                    )}
                  </div>
                )}
              </footer>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
