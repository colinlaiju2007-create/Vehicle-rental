/**
 * ApexDrive — Frontend Application Logic
 * Integrates directly with genuine Spring Boot REST APIs and SQLite backend.
 */

// Global Application State
let allVehicles = [];
let currentCategoryFilter = '';
let currentSort = 'price-asc';
let selectedVehicle = null;
let lastConfirmedBooking = null;
let adminToken = sessionStorage.getItem('apexdrive_admin_token') || null;
let activeAdminTab = 'bookings';
const inrFormatter = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
});

function formatINR(amount) {
  return inrFormatter.format(Number(amount) || 0);
}

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
  initDefaultDates();
  loadVehicles();
  if (adminToken) {
    showAdminDashboard();
  }
});

/**
 * Initializes pickup and return dates with reasonable defaults (today and +2 days).
 */
function initDefaultDates() {
  const today = new Date();
  const returnD = new Date();
  returnD.setDate(today.getDate() + 2);

  const formatDate = (d) => d.toISOString().split('T')[0];

  const searchPickup = document.getElementById('searchPickupDate');
  const searchReturn = document.getElementById('searchReturnDate');
  const bookPickup = document.getElementById('bookPickupDate');
  const bookReturn = document.getElementById('bookReturnDate');

  const todayStr = formatDate(today);
  const returnStr = formatDate(returnD);

  if (searchPickup) {
    searchPickup.min = todayStr;
    searchPickup.value = todayStr;
  }
  if (searchReturn) {
    searchReturn.min = todayStr;
    searchReturn.value = returnStr;
  }
  if (bookPickup) {
    bookPickup.min = todayStr;
    bookPickup.value = todayStr;
  }
  if (bookReturn) {
    bookReturn.min = todayStr;
    bookReturn.value = returnStr;
  }
}

// --- Smooth Navigation ---
function navigateTo(sectionId) {
  const elem = document.getElementById(sectionId);
  if (elem) {
    elem.scrollIntoView({ behavior: 'smooth' });
    document.querySelectorAll('.nav-link').forEach(link => {
      link.classList.remove('active');
      if (link.getAttribute('href') === `#${sectionId}`) {
        link.classList.add('active');
      }
    });
  }
}

function scrollToFleet() {
  navigateTo('fleet');
}

// --- Toast Notifications ---
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
  toast.innerHTML = `<span style="font-weight: bold; font-size: 1.1rem;">${icon}</span><span>${escapeHtml(message)}</span>`;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// --- Fetch Vehicles from Java Backend ---
async function loadVehicles(category = '', maxPrice = '', pickup = '', returnD = '') {
  const vehicleGrid = document.getElementById('vehicleGrid');
  try {
    let url = '/api/vehicles';
    const params = new URLSearchParams();
    if (category) params.append('category', category);
    if (maxPrice) params.append('maxPrice', maxPrice);
    if (pickup) params.append('pickupDate', pickup);
    if (returnD) params.append('returnDate', returnD);

    if (params.toString()) {
      url += `?${params.toString()}`;
    }

    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Server returned HTTP ${response.status}`);
    }

    allVehicles = await response.json();
    renderFleetGrid();
  } catch (err) {
    console.error('Failed to load fleet vehicles:', err);
    vehicleGrid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 3rem 0; color: #f87171;">
        <p>⚠️ Unable to connect to Spring Boot backend (${escapeHtml(err.message)}).</p>
        <p style="font-size: 0.85rem; color: var(--text-dim); margin-top: 0.5rem;">Ensure the Java application is running at http://localhost:8080.</p>
      </div>
    `;
  }
}

// --- Render Fleet Cards ---
function renderFleetGrid() {
  const vehicleGrid = document.getElementById('vehicleGrid');
  const sortValue = document.getElementById('sortSelector')?.value || 'price-asc';

  let list = [...allVehicles];

  // Category filter
  if (currentCategoryFilter) {
    list = list.filter(v => v.category === currentCategoryFilter);
  }

  // Sorting
  if (sortValue === 'price-asc') {
    list.sort((a, b) => a.dailyPrice - b.dailyPrice);
  } else if (sortValue === 'price-desc') {
    list.sort((a, b) => b.dailyPrice - a.dailyPrice);
  } else if (sortValue === 'name-asc') {
    list.sort((a, b) => a.name.localeCompare(b.name));
  }

  if (list.length === 0) {
    vehicleGrid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 4rem 0;">
        <h3 style="color: #fff; font-size: 1.25rem;">No vehicles found</h3>
        <p style="color: var(--text-muted); margin-top: 0.5rem;">Try adjusting your category or budget filters.</p>
        <button class="btn btn-outline btn-sm" style="margin-top: 1rem;" onclick="setFleetFilter('')">Reset Filters</button>
      </div>
    `;
    return;
  }

  vehicleGrid.innerHTML = list.map(v => createVehicleCardHtml(v)).join('');
}

function createVehicleCardHtml(v) {
  const isAvailable = v.available === true;
  const categoryBadgeClass = `badge-${v.category ? v.category.toLowerCase() : 'sedan'}`;
  const categoryLabel = v.category || 'Standard';

  // Fallback image placeholder if offline
  const safeImg = v.imageUrl || 'https://upload.wikimedia.org/wikipedia/commons/7/74/Mahindra_Thar_CRDi_%2816183560860%29.jpg';

  return `
    <article class="vehicle-card" data-id="${v.id}">
      <div class="card-image-wrap">
        <img class="card-image" src="${safeImg}" alt="${escapeHtml(v.name)}" loading="lazy" onerror="this.src='https://upload.wikimedia.org/wikipedia/commons/f/fa/Tata_Nexon_XM.jpg'">
        <span class="card-badge ${categoryBadgeClass}">${categoryLabel}</span>
        <span class="card-status ${isAvailable ? 'status-available' : 'status-booked'}">
          ${isAvailable ? '● Available' : '● Decommissioned'}
        </span>
      </div>

      <div class="card-body">
        <h3 class="card-title">${escapeHtml(v.name)}</h3>
        <p class="card-specs-text">${escapeHtml(v.specifications || 'Precision engineering with premium interior finish.')}</p>

        <div class="card-specs-pills">
          <div class="spec-item">
            <strong>${escapeHtml(v.transmission || 'Automatic')}</strong>
            <span>Gearbox</span>
          </div>
          <div class="spec-item">
            <strong>${v.seatingCapacity || 4} Seats</strong>
            <span>Cabin</span>
          </div>
          <div class="spec-item">
            <strong>${escapeHtml(v.fuelType || 'Petrol')}</strong>
            <span>Powertrain</span>
          </div>
        </div>

        <div class="card-perks-box">
          ✨ <strong>OOP Perk:</strong> ${escapeHtml(v.specialPerks || 'Standard insurance and roadside support.')}
        </div>

        <div class="card-footer">
          <div class="card-price-wrap">
            <span class="price-label">Daily Rental</span>
            <div class="price-value">${formatINR(v.dailyPrice)} <span>/ day</span></div>
          </div>

          <button class="btn btn-primary" onclick="openBookingModal(${v.id})" ${!isAvailable ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>
            ${isAvailable ? 'Reserve Car &rarr;' : 'Unavailable'}
          </button>
        </div>
      </div>
    </article>
  `;
}

// --- Filter Pills Handling ---
function setFleetFilter(category) {
  currentCategoryFilter = category;
  document.querySelectorAll('.filter-pill').forEach(btn => {
    btn.classList.remove('active');
    const text = btn.innerText.toUpperCase();
    if (category === '' && text.includes('ALL')) {
      btn.classList.add('active');
    } else if (category && text.includes(category)) {
      btn.classList.add('active');
    }
  });
  renderFleetGrid();
}

function applyHeroSearch() {
  const category = document.getElementById('searchCategory').value;
  const pickup = document.getElementById('searchPickupDate').value;
  const returnD = document.getElementById('searchReturnDate').value;
  const maxPrice = document.getElementById('searchMaxPrice').value;

  currentCategoryFilter = category;
  loadVehicles(category, maxPrice, pickup, returnD);
  scrollToFleet();
  showToast('Fleet filtered according to your preferences.', 'info');
}

// --- Booking Modal & Real-Time Price Calculation ---
async function openBookingModal(vehicleId) {
  const vehicle = allVehicles.find(v => v.id === vehicleId);
  if (!vehicle) return;

  selectedVehicle = vehicle;
  document.getElementById('bookVehicleId').value = vehicle.id;

  const summary = document.getElementById('modalVehicleSummary');
  summary.innerHTML = `
    <img src="${vehicle.imageUrl || 'https://upload.wikimedia.org/wikipedia/commons/f/fa/Tata_Nexon_XM.jpg'}" class="summary-thumb" alt="${escapeHtml(vehicle.name)}">
    <div class="summary-info">
      <h4>${escapeHtml(vehicle.name)}</h4>
      <p>${formatINR(vehicle.dailyPrice)} / day &bull; ${vehicle.category}</p>
    </div>
  `;

  // Sync dates from hero search if set
  const searchPickup = document.getElementById('searchPickupDate')?.value;
  const searchReturn = document.getElementById('searchReturnDate')?.value;
  if (searchPickup) document.getElementById('bookPickupDate').value = searchPickup;
  if (searchReturn) document.getElementById('bookReturnDate').value = searchReturn;

  document.getElementById('bookingModal').classList.add('active');
  await calculateLivePrice();
}

function closeBookingModal() {
  document.getElementById('bookingModal').classList.remove('active');
  document.getElementById('dateConflictAlert').style.display = 'none';
}

/**
 * Calls backend /api/bookings/calculate-price on date change.
 * Demonstrates real-time Java OOP polymorphic calculations & overlap checking.
 */
async function calculateLivePrice() {
  if (!selectedVehicle) return;

  const vehicleId = selectedVehicle.id;
  const pickupDate = document.getElementById('bookPickupDate').value;
  const returnDate = document.getElementById('bookReturnDate').value;

  if (!pickupDate || !returnDate) return;

  try {
    const response = await fetch('/api/bookings/calculate-price', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ vehicleId, pickupDate, returnDate })
    });

    if (!response.ok) {
      const err = await response.json();
      showToast(err.message || 'Date calculation error', 'error');
      return;
    }

    const quote = await response.json();

    document.getElementById('calcDuration').innerText = `${quote.rentalDays} day${quote.rentalDays > 1 ? 's' : ''}`;
    document.getElementById('calcDailyRate').innerText = `${formatINR(quote.dailyPrice)} / day`;

    const discountRow = document.getElementById('calcDiscountRow');
    const discountEl = document.getElementById('calcDiscount');

    if (quote.discountOrAdjustment < 0) {
      discountEl.innerText = `-${formatINR(Math.abs(quote.discountOrAdjustment))} (Subtype Discount)`;
      discountEl.style.color = 'var(--accent-green)';
    } else if (quote.discountOrAdjustment > 0) {
      discountEl.innerText = `+${formatINR(quote.discountOrAdjustment)} (VIP Service Package)`;
      discountEl.style.color = 'var(--accent-gold)';
    } else {
      discountEl.innerText = `${formatINR(0)} (Standard)`;
      discountEl.style.color = 'var(--text-muted)';
    }

    document.getElementById('calcTotalCost').innerText = formatINR(quote.finalTotal);
    document.getElementById('calcPerksText').innerHTML = `<strong>Included:</strong> ${escapeHtml(quote.perks || '')}`;

    const conflictAlert = document.getElementById('dateConflictAlert');
    const submitBtn = document.getElementById('btnSubmitBooking');

    if (!quote.availableForDates) {
      conflictAlert.style.display = 'block';
      submitBtn.disabled = true;
      submitBtn.style.opacity = '0.5';
    } else {
      conflictAlert.style.display = 'none';
      submitBtn.disabled = false;
      submitBtn.style.opacity = '1';
    }
  } catch (err) {
    console.error('Failed to calculate price quote:', err);
  }
}

// --- Submit Booking to Java Backend ---
async function submitBooking(event) {
  event.preventDefault();

  const vehicleId = parseInt(document.getElementById('bookVehicleId').value);
  const pickupDate = document.getElementById('bookPickupDate').value;
  const returnDate = document.getElementById('bookReturnDate').value;
  const customerName = document.getElementById('bookCustName').value.trim();
  const customerEmail = document.getElementById('bookCustEmail').value.trim();
  const customerPhone = document.getElementById('bookCustPhone').value.trim();
  const driverLicense = document.getElementById('bookDriverLicense').value.trim();
  const specialRequests = document.getElementById('bookSpecialRequests').value.trim();

  const submitBtn = document.getElementById('btnSubmitBooking');
  submitBtn.disabled = true;
  submitBtn.innerText = 'Persisting to SQLite...';

  try {
    const response = await fetch('/api/bookings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        vehicleId,
        customerName,
        customerEmail,
        customerPhone,
        driverLicense,
        pickupDate,
        returnDate,
        specialRequests
      })
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || 'Failed to complete reservation');
    }

    lastConfirmedBooking = data;
    closeBookingModal();
    openConfirmationModal(data);
    showToast(`Reservation ${data.bookingReference} confirmed!`, 'success');

    // Reload fleet to update availability
    loadVehicles();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerText = 'Confirm Reservation →';
  }
}

// --- Confirmation Modal ---
function openConfirmationModal(booking) {
  document.getElementById('confirmRefCode').innerText = booking.bookingReference;

  const details = document.getElementById('confirmSummaryDetails');
  details.innerHTML = `
    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
      <span style="color: var(--text-muted);">Vehicle Reserved:</span>
      <strong style="color: #fff;">${escapeHtml(booking.vehicleName)}</strong>
    </div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
      <span style="color: var(--text-muted);">Customer:</span>
      <span style="color: #fff;">${escapeHtml(booking.customerName)} (${escapeHtml(booking.customerEmail)})</span>
    </div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
      <span style="color: var(--text-muted);">Duration:</span>
      <span style="color: #fff;">${booking.pickupDate} to ${booking.returnDate} (${booking.rentalDays} days)</span>
    </div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
      <span style="color: var(--text-muted);">Perks Applied:</span>
      <span style="color: #93c5fd; font-size: 0.8rem; max-width: 60%; text-align: right;">${escapeHtml(booking.vehiclePerks || 'Standard')}</span>
    </div>
    <div style="display: flex; justify-content: space-between; border-top: 1px solid var(--border-subtle); padding-top: 0.5rem; margin-top: 0.5rem;">
      <strong style="color: #fff;">Total Cost:</strong>
      <strong style="color: var(--accent-cyan); font-size: 1.2rem;">${formatINR(booking.totalRentalCost)}</strong>
    </div>
  `;

  document.getElementById('confirmationModal').classList.add('active');
}

function closeConfirmationModal() {
  document.getElementById('confirmationModal').classList.remove('active');
}

function copyBookingRef() {
  const code = document.getElementById('confirmRefCode').innerText;
  navigator.clipboard.writeText(code);
  showToast('Booking reference copied to clipboard!', 'success');
}

function viewInMyBookings() {
  closeConfirmationModal();
  if (lastConfirmedBooking) {
    document.getElementById('lookupRef').value = lastConfirmedBooking.bookingReference;
    document.getElementById('lookupEmail').value = lastConfirmedBooking.customerEmail;
    lookupBookingRecord();
  }
  navigateTo('my-bookings');
}

// --- Customer Booking Lookup & Cancellation ---
async function lookupBookingRecord() {
  const ref = document.getElementById('lookupRef').value.trim();
  const email = document.getElementById('lookupEmail').value.trim();

  if (!ref || !email) {
    showToast('Please provide both Booking Reference and Customer Email.', 'error');
    return;
  }

  const container = document.getElementById('myBookingsList');
  container.innerHTML = '<p style="text-align: center; color: var(--text-muted);">Searching database...</p>';

  try {
    const res = await fetch(`/api/bookings/lookup?reference=${encodeURIComponent(ref)}&email=${encodeURIComponent(email)}`);
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'No matching reservation found');
    }

    const booking = await res.json();
    container.innerHTML = renderBookingCardHtml(booking);
    showToast('Reservation record retrieved successfully.', 'success');
  } catch (err) {
    container.innerHTML = `
      <div style="text-align: center; padding: 2rem; color: #f87171;">
        <p>⚠️ ${escapeHtml(err.message)}</p>
        <p style="font-size: 0.8rem; color: var(--text-dim); margin-top: 0.5rem;">Verify your booking reference and exact email address.</p>
      </div>
    `;
    showToast(err.message, 'error');
  }
}

async function lookupByEmailOnly() {
  const email = document.getElementById('lookupEmail').value.trim();
  if (!email) {
    showToast('Please enter your Customer Email to search records.', 'error');
    return;
  }

  const container = document.getElementById('myBookingsList');
  container.innerHTML = '<p style="text-align: center; color: var(--text-muted);">Searching reservations for email...</p>';

  try {
    const res = await fetch(`/api/bookings/customer?email=${encodeURIComponent(email)}`);
    if (!res.ok) {
      throw new Error('Failed to retrieve customer bookings');
    }

    const list = await res.json();
    if (list.length === 0) {
      container.innerHTML = `
        <div style="text-align: center; padding: 2rem; color: var(--text-muted);">
          No reservations found for email <strong>${escapeHtml(email)}</strong>.
        </div>
      `;
      return;
    }

    container.innerHTML = list.map(b => renderBookingCardHtml(b)).join('');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderBookingCardHtml(b) {
  const isCancelled = b.status === 'CANCELLED';
  const badgeClass = isCancelled ? 'booking-badge-cancelled' : 'booking-badge-confirmed';
  const safeImg = b.vehicleImageUrl || 'https://upload.wikimedia.org/wikipedia/commons/f/fa/Tata_Nexon_XM.jpg';

  return `
    <div class="booking-item-card">
      <img src="${safeImg}" class="booking-item-thumb" alt="${escapeHtml(b.vehicleName)}">
      
      <div class="booking-item-meta">
        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.25rem;">
          <span style="font-family: monospace; font-weight: bold; color: var(--accent-cyan);">${b.bookingReference}</span>
          <span class="${badgeClass}">${b.status}</span>
        </div>
        <h4>${escapeHtml(b.vehicleName)}</h4>
        <div style="font-size: 0.85rem; color: var(--text-muted);">
          📅 <strong>${b.pickupDate}</strong> to <strong>${b.returnDate}</strong> (${b.rentalDays} days)
        </div>
        <div style="font-size: 0.825rem; color: var(--text-dim); margin-top: 0.25rem;">
          Customer: ${escapeHtml(b.customerName)} &bull; ${escapeHtml(b.customerEmail)}
        </div>
        ${isCancelled && b.cancellationReason ? `
          <div style="font-size: 0.8rem; color: #f87171; margin-top: 0.5rem; background: rgba(239, 68, 68, 0.1); padding: 0.4rem 0.6rem; border-radius: var(--radius-sm);">
            Reason: ${escapeHtml(b.cancellationReason)}
          </div>
        ` : ''}
      </div>

      <div style="text-align: right; display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end; gap: 0.75rem;">
        <div>
          <span style="font-size: 0.75rem; color: var(--text-dim); display: block;">Total Paid / Due</span>
          <strong style="font-size: 1.4rem; color: #fff;">${formatINR(b.totalRentalCost)}</strong>
        </div>

        ${!isCancelled ? `
          <button class="btn btn-danger btn-sm" onclick="promptCustomerCancel('${b.bookingReference}', '${b.customerEmail}')">
            Cancel Booking
          </button>
        ` : `
          <span style="font-size: 0.75rem; color: var(--text-dim);">Record Retained in SQLite</span>
        `}
      </div>
    </div>
  `;
}

async function promptCustomerCancel(ref, email) {
  const reason = prompt(`Are you sure you want to cancel reservation ${ref}?\nPlease provide a cancellation reason (optional):`, 'Schedule conflict');
  if (reason === null) return; // User pressed Cancel

  try {
    const res = await fetch(`/api/bookings/${ref}/cancel`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ customerEmail: email, reason: reason || 'Customer requested cancellation' })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'Cancellation failed');
    }

    showToast(`Reservation ${ref} cancelled successfully.`, 'success');
    lookupBookingRecord();
    loadVehicles(); // Restore calendar availability
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- Admin Portal Logic ---
async function handleAdminLogin(e) {
  e.preventDefault();
  const username = document.getElementById('adminUser').value;
  const password = document.getElementById('adminPass').value;

  try {
    const res = await fetch('/api/admin/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await res.json();
    if (!res.ok || !data.authenticated) {
      throw new Error(data.message || 'Authentication failed');
    }

    adminToken = data.token;
    sessionStorage.setItem('apexdrive_admin_token', adminToken);
    showAdminDashboard();
    showToast('Signed in to Admin Portal.', 'success');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function handleAdminLogout() {
  adminToken = null;
  sessionStorage.removeItem('apexdrive_admin_token');
  document.getElementById('adminLoginPanel').style.display = 'block';
  document.getElementById('adminDashboardView').style.display = 'none';
  showToast('Logged out of Admin Portal.', 'info');
}

function showAdminDashboard() {
  document.getElementById('adminLoginPanel').style.display = 'none';
  document.getElementById('adminDashboardView').style.display = 'block';
  loadAdminStats();
  loadAdminBookings();
  loadAdminVehicles();
}

async function loadAdminStats() {
  if (!adminToken) return;
  try {
    const res = await fetch('/api/admin/stats', {
      headers: { 'X-Admin-Key': adminToken }
    });
    if (!res.ok) return;

    const stats = await res.json();
    document.getElementById('statTotalVehicles').innerText = stats.totalVehicles;
    document.getElementById('statAvailableVehicles').innerText = stats.availableVehicles;
    document.getElementById('statTotalBookings').innerText = stats.totalBookings;
    document.getElementById('statActiveBookings').innerText = stats.activeBookings;
    document.getElementById('statGrossRevenue').innerText = formatINR(stats.totalRevenue);
  } catch (err) {
    console.error('Failed to load admin stats:', err);
  }
}

function switchAdminTab(tab) {
  activeAdminTab = tab;
  const tabBookings = document.getElementById('adminTabBookings');
  const tabVehicles = document.getElementById('adminTabVehicles');
  const btnBookings = document.getElementById('tabBtnBookings');
  const btnVehicles = document.getElementById('tabBtnVehicles');

  if (tab === 'bookings') {
    tabBookings.style.display = 'block';
    tabVehicles.style.display = 'none';
    btnBookings.classList.add('active');
    btnVehicles.classList.remove('active');
    loadAdminBookings();
  } else {
    tabBookings.style.display = 'none';
    tabVehicles.style.display = 'block';
    btnBookings.classList.remove('active');
    btnVehicles.classList.add('active');
    loadAdminVehicles();
  }
}

async function loadAdminBookings() {
  if (!adminToken) return;
  const tbody = document.getElementById('adminBookingsTableBody');
  tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">Loading bookings...</td></tr>';

  try {
    const res = await fetch('/api/admin/bookings', {
      headers: { 'X-Admin-Key': adminToken }
    });
    if (!res.ok) throw new Error('Unauthorized');

    const bookings = await res.json();
    if (bookings.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-dim);">No bookings in database yet.</td></tr>';
      return;
    }

    tbody.innerHTML = bookings.map(b => {
      const isCancelled = b.status === 'CANCELLED';
      return `
        <tr>
          <td><strong style="color: var(--accent-cyan); font-family: monospace;">${b.bookingReference}</strong></td>
          <td>${escapeHtml(b.vehicleName)}</td>
          <td>
            <div>${escapeHtml(b.customerName)}</div>
            <div style="font-size: 0.75rem; color: var(--text-dim);">${escapeHtml(b.customerEmail)}</div>
          </td>
          <td>${b.pickupDate} &rarr; ${b.returnDate} (${b.rentalDays}d)</td>
          <td><strong>${formatINR(b.totalRentalCost)}</strong></td>
          <td><span class="${isCancelled ? 'booking-badge-cancelled' : 'booking-badge-confirmed'}">${b.status}</span></td>
          <td>
            ${!isCancelled ? `
              <button class="btn btn-danger btn-sm" onclick="adminCancelReservation('${b.bookingReference}')">
                Cancel
              </button>
            ` : `<span style="color: var(--text-dim); font-size: 0.8rem;">Cancelled</span>`}
          </td>
        </tr>
      `;
    }).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #f87171;">Error loading reservations: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function adminCancelReservation(ref) {
  const reason = prompt(`Administrative cancellation for ${ref}:\nEnter audit reason:`, 'Administrative fleet reallocation');
  if (reason === null) return;

  try {
    const res = await fetch(`/api/admin/bookings/${ref}/cancel`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Key': adminToken
      },
      body: JSON.stringify({ reason })
    });

    if (!res.ok) throw new Error('Cancellation failed');

    showToast(`Reservation ${ref} cancelled by Administrator.`, 'success');
    loadAdminStats();
    loadAdminBookings();
    loadVehicles();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function loadAdminVehicles() {
  if (!adminToken) return;
  const tbody = document.getElementById('adminVehiclesTableBody');
  tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">Loading fleet...</td></tr>';

  try {
    const res = await fetch('/api/admin/vehicles', {
      headers: { 'X-Admin-Key': adminToken }
    });
    if (!res.ok) throw new Error('Unauthorized');

    const vehicles = await res.json();
    tbody.innerHTML = vehicles.map(v => `
      <tr>
        <td>#${v.id}</td>
        <td>
          <strong>${escapeHtml(v.name)}</strong>
        </td>
        <td><span class="badge-${v.category ? v.category.toLowerCase() : 'sedan'}" style="padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem;">${v.category}</span></td>
        <td>${formatINR(v.dailyPrice)} / day</td>
        <td>${escapeHtml(v.transmission)} &bull; ${escapeHtml(v.fuelType)}</td>
        <td>
          <span style="color: ${v.available ? 'var(--accent-green)' : '#f87171'}; font-weight: bold;">
            ${v.available ? '● Active' : '● Decommissioned'}
          </span>
        </td>
        <td>
          <div style="display: flex; gap: 0.5rem;">
            <button class="btn btn-outline btn-sm" onclick="toggleVehicleStatus(${v.id})">
              ${v.available ? 'Decommission' : 'Activate'}
            </button>
            <button class="btn btn-danger btn-sm" onclick="deleteVehicle(${v.id})">
              Delete
            </button>
          </div>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #f87171;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function toggleVehicleStatus(id) {
  try {
    const res = await fetch(`/api/admin/vehicles/${id}/toggle-status`, {
      method: 'PATCH',
      headers: { 'X-Admin-Key': adminToken }
    });
    if (!res.ok) throw new Error('Failed to toggle status');

    showToast('Vehicle fleet availability toggled.', 'success');
    loadAdminStats();
    loadAdminVehicles();
    loadVehicles();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteVehicle(id) {
  if (!confirm(`Are you sure you want to delete vehicle #${id}? This cannot be undone.`)) return;

  try {
    const res = await fetch(`/api/admin/vehicles/${id}`, {
      method: 'DELETE',
      headers: { 'X-Admin-Key': adminToken }
    });
    if (!res.ok) throw new Error('Failed to delete vehicle');

    showToast('Vehicle removed from fleet database.', 'success');
    loadAdminStats();
    loadAdminVehicles();
    loadVehicles();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function openAddVehicleModal() {
  document.getElementById('vehicleForm').reset();
  document.getElementById('vAdminId').value = '';
  document.getElementById('vehicleModalTitle').innerText = 'Add New Vehicle to Fleet';
  document.getElementById('vehicleModal').classList.add('active');
}

function closeVehicleModal() {
  document.getElementById('vehicleModal').classList.remove('active');
}

async function submitVehicleForm(e) {
  e.preventDefault();

  const payload = {
    name: document.getElementById('vName').value.trim(),
    category: document.getElementById('vCategory').value,
    dailyPrice: parseFloat(document.getElementById('vPrice').value),
    transmission: document.getElementById('vTransmission').value.trim(),
    seatingCapacity: parseInt(document.getElementById('vSeats').value),
    fuelType: document.getElementById('vFuel').value.trim(),
    imageUrl: document.getElementById('vImageUrl').value.trim(),
    specifications: document.getElementById('vSpecs').value.trim(),
    available: true
  };

  try {
    const res = await fetch('/api/admin/vehicles', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Key': adminToken
      },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message || 'Failed to save vehicle');
    }

    closeVehicleModal();
    showToast('New vehicle registered into SQLite database!', 'success');
    loadAdminStats();
    loadAdminVehicles();
    loadVehicles();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
