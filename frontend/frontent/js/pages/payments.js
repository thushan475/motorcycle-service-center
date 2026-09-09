if (!localStorage.getItem('mc_token')) {
  window.location.href = 'index.html';
  throw new Error('Not authenticated');
}

UI.renderShell('payments.html', 'Payments');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';

if (!isAdmin) {
  $('#add-btn').remove();
}

const PAYMENT_METHODS = ['CASH', 'CARD', 'BANK_TRANSFER'];

let allPayments = [];
let allInvoices = [];

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'paymentDate', label: 'Date' },
  { key: 'invoiceNumber', label: 'Invoice', mono: true },
  { key: 'amount', label: 'Amount', render: r => UI.money(r.amount) },
  { key: 'paymentMethod', label: 'Method' },
  { key: 'reference', label: 'Reference' }
];

function rowActions(row) {
  return `
    <button class="btn btn-small" data-view-bill="${row.invoiceId}">View Bill</button>
    <button class="btn btn-small" data-resend-ebill="${row.invoiceId}">Resend E-Bill</button>
  `;
}

function renderList(rows) {
  UI.renderTable('#table-container', columns, rows, rowActions);
  $('[data-view-bill]').on('click', function () {
    openPrintableInvoice(Number($(this).data('view-bill')));
  });
  $('[data-resend-ebill]').on('click', function () {
    const invoiceId = Number($(this).data('resend-ebill'));
    InvoicesApi.sendEbill(invoiceId)
      .then(() => UI.toast('E-bill sent to customer email.', 'success'))
      .catch(err => UI.toast(err.message || 'Failed to send e-bill', 'error'));
  });
}

function load() {
  PaymentsApi.getAll().then(rows => {
    allPayments = rows;
    renderList(rows);
  }).catch(err => UI.toast(err.message, 'error'));
}

function invoiceOptions() {
  return allInvoices.map(i =>
      `<option value="${i.id}">${UI.escapeHtml(i.invoiceNumber)} — ${UI.escapeHtml(i.customerName)} (balance up to ${UI.money(i.totalAmount).replace(/<[^>]+>/g,'')})</option>`
  ).join('');
}

function remainingBalance(invoiceId) {
  const invoice = allInvoices.find(i => i.id === invoiceId);
  if (!invoice) return Promise.resolve(0);
  return PaymentsApi.getByInvoice(invoiceId).then(payments => {
    const alreadyPaid = (payments || []).reduce((sum, p) => sum + Number(p.amount), 0);
    const remaining = Number(invoice.totalAmount) - alreadyPaid;
    return remaining > 0 ? remaining : 0;
  });
}

function openCreateForm() {
  const html = `
    <div class="form-field">
      <label>Invoice</label>
      <select data-field="invoiceId" required>
        <option value="">Select an invoice</option>
        ${invoiceOptions()}
      </select>
    </div>
    <div class="form-field">
      <label>Amount</label>
      <input type="number" step="0.01" data-field="amount" required>
      <span class="hint">Must not exceed the invoice's remaining balance.</span>
    </div>
    <div class="form-field">
      <label>Payment method</label>
      <select data-field="paymentMethod" required>
        ${PAYMENT_METHODS.map(m => `<option value="${m}">${m.replace('_', ' ')}</option>`).join('')}
      </select>
    </div>
    <div class="form-field">
      <label>Reference</label>
      <input type="text" data-field="reference" placeholder="e.g. receipt or transaction number">
    </div>
  `;
  UI.openDrawer('Record Payment', html, (values) => {
    return PaymentsApi.create({
      invoiceId: Number(values.invoiceId),
      amount: Number(values.amount),
      paymentMethod: values.paymentMethod,
      reference: values.reference
    }).then(() => {
      UI.toast('Payment recorded. E-bill email will be sent to customer.', 'success');
      load();
      showPrintInvoiceButton(Number(values.invoiceId));
    });
  }, 'Record');

  $('[data-field="invoiceId"]').on('change', function () {
    const invoiceId = Number($(this).val());
    const $amount = $('[data-field="amount"]');
    if (!invoiceId) { $amount.val(''); return; }
    remainingBalance(invoiceId).then(balance => {
      $amount.val(balance.toFixed(2));
    });
  });
}

function showPrintInvoiceButton(invoiceId) {
  $('#print-action-bar').remove();
  const bar = $(`
    <div id="print-action-bar" style="position:fixed;bottom:24px;right:24px;z-index:9999;background:#111;color:#fff;padding:14px 18px;border-radius:10px;box-shadow:0 8px 24px rgba(0,0,0,.25);display:flex;gap:10px;align-items:center;flex-wrap:wrap;">
      <span style="font-size:14px;">Payment successful</span>
      <button class="btn btn-primary" id="print-invoice-btn" style="margin:0;">Print Invoice</button>
      <button class="btn btn-primary" id="resend-ebill-btn" style="margin:0;">Resend E-Bill</button>
      <button class="btn" id="close-print-bar" style="margin:0;background:#333;color:#fff;">Close</button>
    </div>
  `);
  $('body').append(bar);
  $('#print-invoice-btn').on('click', function () {
    openPrintableInvoice(invoiceId);
  });
  $('#resend-ebill-btn').on('click', function () {
    InvoicesApi.sendEbill(invoiceId)
      .then(() => UI.toast('E-bill sent to customer email.', 'success'))
      .catch(err => UI.toast(err.message || 'Failed to send e-bill', 'error'));
  });
  $('#close-print-bar').on('click', function () {
    $('#print-action-bar').remove();
  });
}

function openPrintableInvoice(invoiceId) {
  InvoicesApi.getDetail(invoiceId).then(inv => {
    const servicesRows = (inv.services || []).map(s =>
      `<tr><td>${escapeHtml(s.serviceName)}</td><td>${s.quantity}</td><td>${Number(s.unitPrice).toFixed(2)}</td><td>${Number(s.subtotal).toFixed(2)}</td></tr>`
    ).join('');
    const partsRows = (inv.spareParts || []).map(p =>
      `<tr><td>${escapeHtml(p.sparePartName)}</td><td>${p.quantity}</td><td>${Number(p.unitPrice).toFixed(2)}</td><td>${Number(p.subtotal).toFixed(2)}</td></tr>`
    ).join('');
    const paymentsRows = (inv.payments || []).map(p =>
      `<tr><td>${p.paymentDate}</td><td>${p.paymentMethod}</td><td>${Number(p.amount).toFixed(2)}</td><td>${escapeHtml(p.reference || '-')}</td></tr>`
    ).join('');

    const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Invoice ${escapeHtml(inv.invoiceNumber)}</title>
<style>
  body { font-family: Arial, sans-serif; padding: 28px; color: #222; max-width: 900px; margin: 0 auto; }
  h1 { margin: 0 0 4px; font-size: 28px; }
  h3 { margin: 22px 0 8px; border-bottom: 1px solid #ddd; padding-bottom: 4px; }
  .meta p, .block p { margin: 3px 0; }
  table { width: 100%; border-collapse: collapse; margin: 10px 0 6px; font-size: 14px; }
  th, td { border: 1px solid #ccc; padding: 8px 10px; text-align: left; }
  th { background: #f3f3f3; }
  .totals { margin-top: 18px; text-align: right; font-size: 15px; }
  .totals p { margin: 4px 0; }
  .totals strong { font-size: 16px; }
  .toolbar { margin-bottom: 18px; display:flex; gap:10px; }
  @media print { .toolbar { display: none; } body { padding: 0; } }
</style>
</head>
<body>
  <div class="toolbar">
    <button onclick="window.print()" style="padding:10px 18px;font-size:14px;cursor:pointer;">Print Invoice</button>
  </div>
  <h1>INVOICE</h1>
  <div class="meta">
    <p><strong>Invoice No:</strong> ${escapeHtml(inv.invoiceNumber)}</p>
    <p><strong>Date:</strong> ${inv.invoiceDate}</p>
    <p><strong>Status:</strong> ${inv.status}</p>
  </div>
  <h3>Customer</h3>
  <div class="block">
    <p><strong>${escapeHtml(inv.customerName || '')}</strong></p>
    <p>${escapeHtml(inv.customerPhone || '')}</p>
    <p>${escapeHtml(inv.customerEmail || '')}</p>
    <p>${escapeHtml(inv.customerAddress || '')}</p>
  </div>
  <h3>Motorcycle</h3>
  <div class="block">
    <p>${escapeHtml(inv.motorcycleRegistration || '')} | ${escapeHtml(inv.motorcycleBrand || '')} | ${escapeHtml(inv.motorcycleModelYear || '')}</p>
  </div>
  <h3>Services</h3>
  <table>
    <thead><tr><th>Service</th><th>Qty</th><th>Unit Price (LKR)</th><th>Subtotal (LKR)</th></tr></thead>
    <tbody>${servicesRows || '<tr><td colspan="4">None</td></tr>'}</tbody>
  </table>
  <h3>Spare Parts</h3>
  <table>
    <thead><tr><th>Part</th><th>Qty</th><th>Unit Price (LKR)</th><th>Subtotal (LKR)</th></tr></thead>
    <tbody>${partsRows || '<tr><td colspan="4">None</td></tr>'}</tbody>
  </table>
  <div class="totals">
    <p>Subtotal: LKR ${Number(inv.subtotal).toFixed(2)}</p>
    <p>Discount: LKR ${Number(inv.discount || 0).toFixed(2)}</p>
    <p><strong>Total: LKR ${Number(inv.totalAmount).toFixed(2)}</strong></p>
    <p>Paid: LKR ${Number(inv.totalPaid || 0).toFixed(2)}</p>
    <p><strong>Balance: LKR ${Number(inv.balance || 0).toFixed(2)}</strong></p>
  </div>
  <h3>Payments</h3>
  <table>
    <thead><tr><th>Date</th><th>Method</th><th>Amount (LKR)</th><th>Reference</th></tr></thead>
    <tbody>${paymentsRows || '<tr><td colspan="4">None</td></tr>'}</tbody>
  </table>
</body>
</html>`;

    const w = window.open('', '_blank', 'width=900,height=750');
    w.document.write(html);
    w.document.close();
  }).catch(err => UI.toast(err.message || 'Failed to load invoice detail', 'error'));
}

function escapeHtml(str) {
  if (str == null) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

$('#add-btn').on('click', openCreateForm);

$('#search-input').on('input', function () {
  const q = $(this).val().toLowerCase();
  renderList(allPayments.filter(p => (p.invoiceNumber || '').toLowerCase().includes(q)));
});

Promise.all([InvoicesApi.getAll()]).then(([invoices]) => {
  allInvoices = invoices;
  load();
}).catch(err => UI.toast(err.message, 'error'));
