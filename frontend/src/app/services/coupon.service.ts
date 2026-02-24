import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { environment } from '../../environments/environment';
import { Coupon, CouponRequest } from '../models/coupon.model';

@Injectable()
export class CouponService {
  private apiUrl = `${environment.apiUrl}/coupon`;

  constructor(private http: Http) { }

  createCoupon(coupon: CouponRequest): Observable<Coupon> {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers: headers });

    return this.http.post(this.apiUrl, coupon, options)
      .map((response: Response) => response.json())
      .catch(this.handleError);
  }

  getCouponById(id: string): Observable<Coupon> {
    return this.http.get(`${this.apiUrl}/${id}`)
      .map((response: Response) => response.json())
      .catch(this.handleError);
  }

  deleteCoupon(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`)
      .map((response: Response) => response)
      .catch(this.handleError);
  }

  private handleError(error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.message || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
