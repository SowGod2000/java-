import { createRouter, createWebHistory } from 'vue-router'

//layout
import FrontendLayout from '../layouts/FrontendLayout.vue'
import BackendLayout from '../layouts/BackendLayout.vue'
import RestaurantLayout from '@/layouts/RestaurantLayout.vue'

//前台
import HomePage from '../pages/front/HomePage.vue'
//會員
import LoginPage from '../pages/front/member/LoginPage.vue'
import RegisterPage from '../pages/front/member/RegisterPage.vue'
import profile from '../pages/front/member/Profile.vue'
import resetpassword from '../pages/front/member/reset-password.vue'
import forgotPassword from '@/pages/front/member/forgotPassword.vue'
import LoginSuccess from '@/pages/front/member/LoginSuccess.vue'
import AccountDisabled from '@/pages/front/member/AccountDisabled.vue'
import VerifyOtp from '@/pages/front/member/VerifyOtp.vue'
import MyOrders from '@/pages/front/member/MyOrders.vue'
import MemberCenter from '@/pages/front/member/MemberCenter.vue'
import ChangePassword from '@/pages/front/member/ChangePassword.vue'
//餐廳
import RestaurantReservePage from '../pages/front/restaurant/RestaurantReservePage.vue'
import RestaurantQueryPage from '../pages/front/restaurant/RestaurantQueryPage.vue'
import RestaurantOrdersTab from '../pages/front/restaurant/RestaurantOrdersTab.vue'
//飯店
import RoomsPage from '../pages/front/room/RoomsPage.vue'
//電商
import ShopPage from '../pages/front/product/ShopPage.vue'
import CartPage from '../pages/front/product/CartPage.vue'
import OrderPage from '../pages/front/product/OrderPage.vue'
import PaymentSuccessPage from '../pages/front/product/PaymentSuccessPage.vue'
import PaymentFailPage from '../pages/front/product/PaymentFailPage.vue'
import PaymentTestPage from '../pages/front/product/PaymentTestPage.vue'



//後台
import BackEndHomePage from '../pages/backend/BackEndHomePage.vue'


//會員後台
import MemberList from '@/pages/backend/member/MemberList.vue'
import UpdateMember from '@/pages/backend/member/UpdateMember.vue'
//room後台
import RoomtypePage from '../pages/backend/room/RoomtypePage.vue'
import BookingOrderPage from '../pages/backend/room/BookingOrderPage.vue'
//餐廳後台
import RestaurantOrderPage from '../pages/backend/restaurant/RestaurantOrderPage.vue'
import RestaurantOrderChartPage from '../pages/backend/restaurant/RestaurantOrderChartPage.vue'
//電商後台
import ProductAdminPage from '../pages/backend/product/ProductAdminPage.vue'
import OrderAdminPage from '../pages/backend/product/OrderAdminPage.vue'
import TagAdminPage from '../pages/backend/product/TagAdminPage.vue'
import CategoryAdminPage from '../pages/backend/product/CategoryAdminPage.vue'
import DashboardPage from '../pages/backend/product/DashboardPage.vue'


const routes = [
  {
    //前台
    path: '/',
    component: FrontendLayout,
    children: [
      { path: '', component: HomePage },
      //會員
      { path: 'login', component: LoginPage },
      { path: 'register', component: RegisterPage },
      { path: 'profile', component: profile },
      { path: 'MyOrders', component: MyOrders },
      { path: 'MemberCenter', component: MemberCenter },
      { path: 'ChangePassword', component: ChangePassword },
      { path: 'resetpassword', component: resetpassword },
      { path: 'forgotPassword', component: forgotPassword },
      { path: 'loginsuccess', component: LoginSuccess },
      { path: 'AccountDisabled', component: AccountDisabled },
      { path: 'Verifyotp', component: VerifyOtp },
      //餐廳
      {
        path: 'restaurant', component: RestaurantLayout,
        children: [
          { path: 'restaurantReserve', component: RestaurantReservePage },
          { path: 'restaurantQueryPage', component: RestaurantQueryPage },
          { path: 'restaurantOrdersTab', component: RestaurantOrdersTab },
        ]
      },
      //電商
      { path: 'payment-success', component: PaymentSuccessPage },
      { path: 'payment-fail', component: PaymentFailPage },
      { path: 'pay', component: PaymentTestPage },
      { path: 'shop', component: ShopPage },
      { path: 'cart', component: CartPage },
      { path: 'order/:id', component: OrderPage },

      //飯店
      { path: 'rooms', component: RoomsPage },
      {
        path: 'rooms/booking',
        component: () => import('../pages/front/room/BookingPage.vue'),
        name: 'bookingPage'
      },

    ],
  },
  {
    //後台
    path: '/backend',
    component: BackendLayout,
    children: [
      { path: '', component: BackEndHomePage },
      { path: 'roomtype', component: RoomtypePage },
      { path: 'bookingOrder', component: BookingOrderPage },
      //會員管理
      { path: '/UpdateMember', component: UpdateMember },
      { path: 'memberlist', component: MemberList },
      //房型管理
      { path: 'roomtype', component: RoomtypePage },
      { path: 'bookingOrder', component: BookingOrderPage },
      {
        path: 'roomtype/update/:id',
        component: () => import('../pages/backend/room/UpdateRoomtypePage.vue'),
        name: 'UpdateRoomtype'
      },
      {
        path: 'roomtype/insert',
        component: () => import('../pages/backend/room/InsertRoomtypePage.vue'),
        name: 'InsertRoomtype'
      },
      {
        path: '/bookingOrder/update/:id',
        component: () => import('../pages/backend/room/UpdateBookingOrderPage.vue'),
        name: 'UpdateBookingOrder'
      },
      {
        path: 'findRoom',
        component: () => import('../pages/backend/room/FindRoom.vue'),
        name: 'findRoom'
      },
      {
        path: 'booking',
        component: () => import('../pages/backend/room/BookingPage.vue'),
        name: 'booking'
      },
      {
        path: 'bookingChart',
        component: () => import('../pages/backend/room/BookingOrderChart.vue'),
        name: 'bookingChart'
      },
      //餐廳管理
      {
        path: 'restaurant', component: RestaurantLayout,
        children: [
          { path: 'restaurantOrder', component: RestaurantOrderPage },
          { path: 'restaurantOrderChart', component: RestaurantOrderChartPage },
        ]
      },
      //電商管理
      { path: 'product', component: ProductAdminPage },
      { path: 'order', component: OrderAdminPage },
      { path: 'tags', component: TagAdminPage },
      { path: 'categories', component: CategoryAdminPage },
      { path: 'dashboard', component: DashboardPage },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
