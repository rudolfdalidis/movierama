export function setSession({ token, user }) {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));
  }
  
  export function clearSession() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  }
  
  export function getStoredUser() {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
  }
  
  export function isLoggedIn() {
    return !!localStorage.getItem("token");
  }
  