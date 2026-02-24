import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CouponService } from '../../services/coupon.service';
import { Coupon } from '../../models/coupon.model';

@Component({
  selector: 'app-coupon-detail',
  templateUrl: './coupon-detail.component.html',
  styleUrls: ['./coupon-detail.component.css']
})
export class CouponDetailComponent implements OnInit {
  coupon: Coupon | null = null;
  loading: boolean = true;
  errorMessage: string = '';
  showDeleteConfirm: boolean = false;
  deleting: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private couponService: CouponService
  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCoupon(id);
    }
  }

  loadCoupon(id: string) {
    this.loading = true;
    this.errorMessage = '';

    this.couponService.getCouponById(id).subscribe(
      response => {
        this.coupon = response;
        this.loading = false;
      },
      error => {
        this.loading = false;
        this.errorMessage = 'Cupom não encontrado';
      }
    );
  }

  confirmDelete() {
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
  }

  deleteCoupon() {
    if (!this.coupon || !this.coupon.id) return;

    this.deleting = true;
    this.couponService.deleteCoupon(this.coupon.id).subscribe(
      () => {
        this.deleting = false;
        this.router.navigate(['/']);
      },
      error => {
        this.deleting = false;
        this.errorMessage = 'Erro ao deletar cupom';
        this.showDeleteConfirm = false;
      }
    );
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'status-active';
      case 'INACTIVE':
        return 'status-inactive';
      case 'DELETED':
        return 'status-deleted';
      default:
        return '';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'Ativo';
      case 'INACTIVE':
        return 'Inativo';
      case 'DELETED':
        return 'Deletado';
      default:
        return status;
    }
  }
}
