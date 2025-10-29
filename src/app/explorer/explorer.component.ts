import { Component, OnInit } from '@angular/core';
import { ToolDto } from '../dtos/ToolDto';
import { UserDto } from '../dtos/UserDto';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-explorer',
  imports: [CommonModule],
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.css']
})
export class ExplorerComponent implements OnInit {

availableTools: ToolDto[] = [];

  ngOnInit() {
    const user: UserDto = {
      firstName: 'John',
      lastName: 'Doe',
      address: 'Street 123, 1000 New City'
    };

    const tool1: ToolDto = {
      name: 'Hammer',
      picture: undefined,
      description: 'Old trusty hammer',
      owner: user,
      status: 'available'
    };

    this.availableTools = [tool1];
  }
}
