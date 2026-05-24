import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { Home } from './components/home/home';
import { Dashboard } from './components/home/dashboard/dashboard';
import { BillsList } from './components/home/bills/bills';
import { BillSummary } from './components/home/summary/summary';
import { PayBill } from './components/home/pay/pay';
import { InvoiceView } from './components/home/invoice/invoice';
import { BillHistory } from './components/home/history/history';
import { RegisterComplaint } from './components/home/register-complaint/register-complaint';
import { ComplaintStatus } from './components/home/complaint-status/complaint-status';
import { ComplaintHistory } from './components/home/complaints/complaints';
import { AdminDashboard } from './components/admin/admin';
import { AdminConsumersList } from './components/admin/consumers/consumers';
import { AdminAddCustomer } from './components/admin/add-customer/add-customer';
import { AdminAddConsumer } from './components/admin/add-consumer/add-consumer';
import { AdminBillsList } from './components/admin/bills/bills';
import { AdminComplaintsList } from './components/admin/complaints/complaints';
import { AdminOverview } from './components/admin/overview/overview';
import { SmeDashboard } from './components/sme/sme';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { 
    path: 'home', 
    component: Home,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'bills', component: BillsList },
      { path: 'summary', component: BillSummary },
      { path: 'pay', component: PayBill },
      { path: 'invoice/:transactionId', component: InvoiceView },
      { path: 'history', component: BillHistory },
      { path: 'register-complaint', component: RegisterComplaint },
      { path: 'complaint-status', component: ComplaintStatus },
      { path: 'complaints', component: ComplaintHistory }
    ]
  },
  { 
    path: 'admin', 
    component: AdminDashboard,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: AdminOverview },
      { path: 'consumers', component: AdminConsumersList },
      { path: 'add-customer', component: AdminAddCustomer },
      { path: 'add-consumer', component: AdminAddConsumer },
      { path: 'bills', component: AdminBillsList },
      { path: 'complaints', component: AdminComplaintsList }
    ]
  },
  { 
    path: 'sme', 
    component: SmeDashboard,
    canActivate: [roleGuard],
    data: { roles: ['SME'] }
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
