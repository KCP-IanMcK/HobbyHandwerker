import { Routes } from '@angular/router';
import {AppComponent} from './app.component';
import {CreateProfileComponent} from './create-profile/create-profile.component';
import {HomeComponent} from './home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent }, // Default route (Home Page)
  { path: 'create-profile', component: CreateProfileComponent }, // Separate page for Create Profile
];
