import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CouponService } from '../../services/coupon.service';
import { CouponRequest } from '../../models/coupon.model';

@Component({
  selector: 'app-coupon-form',
  templateUrl: './coupon-form.component.html',
  styleUrls: ['./coupon-form.component.css']
})
export class CouponFormComponent implements OnInit {
  coupon: CouponRequest = {
    code: '',
    description: '',
    discountValue: 0.5,
    expirationDate: '',
    published: false
  };

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(
    private couponService: CouponService,
    private router: Router
  ) { }

  ngOnInit() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.coupon.expirationDate = this.formatDateForInput(tomorrow);
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';
    this.loading = true;

    const formattedCoupon = {
      ...this.coupon,
      expirationDate: this.formatDateForApi(this.coupon.expirationDate)
    };

    this.couponService.createCoupon(formattedCoupon).subscribe(
      response => {
        this.loading = false;
        this.successMessage = 'Cupom criado com sucesso!';
        setTimeout(() => {
          this.router.navigate(['/coupon', response.id]);
        }, 1500);
      },
      error => {
        this.loading = false;
        this.errorMessage = this.extractErrorMessage(error);
      }
    );
  }

  private formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private formatDateForApi(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString();
  }

  private extractErrorMessage(error: string): string {
    try {
      const errorObj = JSON.parse(error.split(' - ')[2]);
      return errorObj.message || 'Erro ao criar cupom';
    } catch (e) {
      return 'Erro ao criar cupom. Verifique os dados e tente novamente.';
    }
  }
}
