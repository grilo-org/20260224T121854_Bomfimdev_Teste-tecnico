import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CouponListComponent } from './components/coupon-list/coupon-list.component';
import { CouponFormComponent } from './components/coupon-form/coupon-form.component';
import { CouponDetailComponent } from './components/coupon-detail/coupon-detail.component';

const routes: Routes = [
  { path: '', component: CouponListComponent },
  { path: 'create', component: CouponFormComponent },
  { path: 'coupon/:id', component: CouponDetailComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
