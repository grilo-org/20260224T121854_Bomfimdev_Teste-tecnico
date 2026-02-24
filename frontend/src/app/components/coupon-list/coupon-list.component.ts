import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-coupon-list',
  templateUrl: './coupon-list.component.html',
  styleUrls: ['./coupon-list.component.css']
})
export class CouponListComponent implements OnInit {
  searchId: string = '';

  constructor(private router: Router) { }

  ngOnInit() {
  }

  searchCoupon() {
    if (this.searchId.trim()) {
      this.router.navigate(['/coupon', this.searchId.trim()]);
    }
  }

  createCoupon() {
    this.router.navigate(['/create']);
  }
}
