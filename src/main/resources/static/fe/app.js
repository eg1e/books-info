const API_BASE = window.location.origin.replace(/\/fe$/, "");
let jwtToken = null;

// Login
const loginForm = document.getElementById("login-form");
const loginMessage = document.getElementById("login-message");
const booksSection = document.getElementById("books-section");
const authSection = document.getElementById("auth-section");
const registerSection = document.getElementById("register-section");

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  loginMessage.textContent = "";
  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });
    const data = await res.json();
    if (res.ok && data.token) {
      jwtToken = data.token;
      authSection.style.display = "none";
      registerSection.style.display = "none";
      booksSection.style.display = "block";
    } else {
      loginMessage.textContent = data.message || "Login failed";
    }
  } catch (err) {
    loginMessage.textContent = "Network error";
  }
});

// Load Books
const loadBooksBtn = document.getElementById("load-books");
const booksList = document.getElementById("books-list");
loadBooksBtn.addEventListener("click", async () => {
  booksList.innerHTML = "";
  try {
    const res = await fetch(`${API_BASE}/books`, {
      headers: { Authorization: `Bearer ${jwtToken}` },
    });
    const books = await res.json();
    for (const book of books) {
      await displayBook(book);
    }
  } catch (err) {
    booksList.innerHTML = "<li>Error loading books</li>";
  }
});

async function displayBook(book) {
  const li = document.createElement("li");
  li.className = "book-item";

  // Fetch average rating
  let avgRating = "No ratings";
  try {
    const ratingRes = await fetch(`${API_BASE}/ratings/average/${book.id}`, {
      headers: { Authorization: `Bearer ${jwtToken}` },
    });
    if (ratingRes.ok) {
      const avg = await ratingRes.json();
      avgRating = avg ? `★ ${avg.toFixed(1)}/5` : "No ratings";
    }
  } catch (err) {
    avgRating = "N/A";
  }

  li.innerHTML = `
    <div class="book-info">
      <strong>${book.title}</strong> by ${book.author} (${book.publishedYear})<br>
      ${book.price} EUR | ${book.pages} pages | Rating: ${avgRating}
    </div>
    <div class="book-actions">
      <input type="number" min="1" max="5" placeholder="Rate 1-5" class="rating-input" data-book-id="${book.id}">
      <button class="btn-rate" data-book-id="${book.id}">Rate</button>
      <button class="btn-edit" data-book-id="${book.id}">Edit</button>
      <button class="btn-delete" data-book-id="${book.id}">Delete</button>
    </div>
  `;
  booksList.appendChild(li);
}

// Add Book
const addBookForm = document.getElementById("add-book-form");
const addBookMessage = document.getElementById("add-book-message");
addBookForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  addBookMessage.textContent = "";
  const book = {
    title: document.getElementById("title").value,
    author: document.getElementById("author").value,
    publishedYear: parseInt(document.getElementById("publishedYear").value),
    price: parseInt(document.getElementById("price").value),
    pages: parseInt(document.getElementById("pages").value),
  };
  try {
    const res = await fetch(`${API_BASE}/books`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`,
      },
      body: JSON.stringify(book),
    });
    if (res.ok) {
      addBookMessage.style.color = "green";
      addBookMessage.textContent = "Book added!";
      addBookForm.reset();
    } else {
      const data = await res.json();
      addBookMessage.style.color = "#d9534f";
      addBookMessage.textContent = data.message || "Failed to add book";
    }
  } catch (err) {
    addBookMessage.textContent = "Network error";
  }
});

// Apply Filters
const applyFiltersBtn = document.getElementById("apply-filters");
const clearFiltersBtn = document.getElementById("clear-filters");

applyFiltersBtn.addEventListener("click", async () => {
  booksList.innerHTML = "";
  const title = document.getElementById("filter-title").value;
  const author = document.getElementById("filter-author").value;
  const minPrice = document.getElementById("filter-minPrice").value;
  const maxPrice = document.getElementById("filter-maxPrice").value;
  const minPages = document.getElementById("filter-minPages").value;
  const maxPages = document.getElementById("filter-maxPages").value;
  const minPublishedYear = document.getElementById(
    "filter-minPublishedYear",
  ).value;
  const maxPublishedYear = document.getElementById(
    "filter-maxPublishedYear",
  ).value;

  const params = new URLSearchParams();
  if (title) params.append("title", title);
  if (author) params.append("author", author);
  if (minPrice) params.append("minPrice", minPrice);
  if (maxPrice) params.append("maxPrice", maxPrice);
  if (minPages) params.append("minPages", minPages);
  if (maxPages) params.append("maxPages", maxPages);
  if (minPublishedYear) params.append("minPublishedYear", minPublishedYear);
  if (maxPublishedYear) params.append("maxPublishedYear", maxPublishedYear);

  try {
    const res = await fetch(`${API_BASE}/books?${params.toString()}`, {
      headers: { Authorization: `Bearer ${jwtToken}` },
    });
    const books = await res.json();
    if (books.length === 0) {
      booksList.innerHTML = "<li>No books found matching your filters</li>";
    } else {
      for (const book of books) {
        await displayBook(book);
      }
    }
  } catch (err) {
    booksList.innerHTML = "<li>Error loading books: " + err.message + "</li>";
  }
});

clearFiltersBtn.addEventListener("click", () => {
  document.getElementById("filter-title").value = "";
  document.getElementById("filter-author").value = "";
  document.getElementById("filter-minPrice").value = "";
  document.getElementById("filter-maxPrice").value = "";
  document.getElementById("filter-minPages").value = "";
  document.getElementById("filter-maxPages").value = "";
  document.getElementById("filter-minPublishedYear").value = "";
  document.getElementById("filter-maxPublishedYear").value = "";
  booksList.innerHTML = "";
});

// Register
const registerForm = document.getElementById("register-form");
const registerMessage = document.getElementById("register-message");

registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("register-username").value;
  const email = document.getElementById("register-email").value;
  const password = document.getElementById("register-password").value;
  registerMessage.textContent = "";
  try {
    const res = await fetch(`${API_BASE}/api/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password }),
    });
    const data = await res.json();
    if (res.ok) {
      registerMessage.textContent =
        "Registration successful! You can now log in.";
      registerForm.reset();
    } else {
      registerMessage.textContent = data.message || "Registration failed.";
    }
  } catch (err) {
    registerMessage.textContent = "Network error.";
  }
});

// Rate Book
booksList.addEventListener("click", async (e) => {
  if (e.target.classList.contains("btn-rate")) {
    const bookId = e.target.dataset.bookId;
    const ratingInput = document.querySelector(
      `.rating-input[data-book-id="${bookId}"]`,
    );
    const rating = parseInt(ratingInput.value);

    if (!rating || rating < 1 || rating > 5) {
      alert("Please enter a rating between 1 and 5");
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/ratings/${bookId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${jwtToken}`,
        },
        body: JSON.stringify({ rating }),
      });

      if (res.ok) {
        alert("Rating added successfully!");
        ratingInput.value = "";
        loadBooksBtn.click(); // Reload books to show updated rating
      } else {
        alert("Failed to add rating");
      }
    } catch (err) {
      alert("Network error");
    }
  }
});

// Delete Book
booksList.addEventListener("click", async (e) => {
  if (e.target.classList.contains("btn-delete")) {
    const bookId = e.target.dataset.bookId;
    if (!confirm("Are you sure you want to delete this book?")) return;

    try {
      const res = await fetch(`${API_BASE}/books/${bookId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${jwtToken}` },
      });

      if (res.ok) {
        alert("Book deleted successfully!");
        loadBooksBtn.click(); // Reload books
      } else {
        alert("Failed to delete book");
      }
    } catch (err) {
      alert("Network error");
    }
  }
});

// Edit Book - Show Update Form
booksList.addEventListener("click", async (e) => {
  if (e.target.classList.contains("btn-edit")) {
    const bookId = e.target.dataset.bookId;

    try {
      const res = await fetch(`${API_BASE}/books`, {
        headers: { Authorization: `Bearer ${jwtToken}` },
      });
      const books = await res.json();
      const book = books.find((b) => b.id === parseInt(bookId));

      if (book) {
        document.getElementById("update-id").value = book.id;
        document.getElementById("update-title").value = book.title;
        document.getElementById("update-author").value = book.author;
        document.getElementById("update-publishedYear").value =
          book.publishedYear;
        document.getElementById("update-price").value = book.price;
        document.getElementById("update-pages").value = book.pages;
        document.getElementById("update-book-section").style.display = "block";
        document
          .getElementById("update-book-section")
          .scrollIntoView({ behavior: "smooth" });
      }
    } catch (err) {
      alert("Error loading book data");
    }
  }
});

// Update Book Form
const updateBookForm = document.getElementById("update-book-form");
const updateBookMessage = document.getElementById("update-book-message");
const updateBookSection = document.getElementById("update-book-section");

updateBookForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  updateBookMessage.textContent = "";

  const book = {
    id: parseInt(document.getElementById("update-id").value),
    title: document.getElementById("update-title").value,
    author: document.getElementById("update-author").value,
    publishedYear: parseInt(
      document.getElementById("update-publishedYear").value,
    ),
    price: parseInt(document.getElementById("update-price").value),
    pages: parseInt(document.getElementById("update-pages").value),
  };

  try {
    const res = await fetch(`${API_BASE}/books`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwtToken}`,
      },
      body: JSON.stringify(book),
    });

    if (res.ok) {
      updateBookMessage.style.color = "green";
      updateBookMessage.textContent = "Book updated successfully!";
      setTimeout(() => {
        updateBookSection.style.display = "none";
        updateBookForm.reset();
        loadBooksBtn.click(); // Reload books
      }, 1500);
    } else {
      const data = await res.json();
      updateBookMessage.style.color = "#d9534f";
      updateBookMessage.textContent = data.message || "Failed to update book";
    }
  } catch (err) {
    updateBookMessage.textContent = "Network error";
  }
});

// Cancel Update
document.getElementById("cancel-update").addEventListener("click", () => {
  updateBookSection.style.display = "none";
  updateBookForm.reset();
});
