import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CouponService } from './services/coupon.service';
import { CouponListComponent } from './components/coupon-list/coupon-list.component';
import { CouponFormComponent } from './components/coupon-form/coupon-form.component';
import { CouponDetailComponent } from './components/coupon-detail/coupon-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    CouponListComponent,
    CouponFormComponent,
    CouponDetailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [CouponService],
  bootstrap: [AppComponent]
})
export class AppModule { }
