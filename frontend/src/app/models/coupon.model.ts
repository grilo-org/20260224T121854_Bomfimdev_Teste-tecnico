export interface Coupon {
  id?: string;
  code: string;
  description: string;
  discountValue: number;
  expirationDate: string;
  status?: CouponStatus;
  published?: boolean;
  redeemed?: boolean;
}

export enum CouponStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  DELETED = 'DELETED'
}

export interface CouponRequest {
  code: string;
  description: string;
  discountValue: number;
  expirationDate: string;
  published?: boolean;
}
