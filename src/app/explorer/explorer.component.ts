import { Component, OnInit } from '@angular/core';
import { ToolDto } from '../dtos/ToolDto';
import { UserDto } from '../dtos/UserDto';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ToolDetailComponent} from '../tool-detail/tool-detail.component';


@Component({
  selector: 'app-explorer',
  imports: [CommonModule, ToolDetailComponent],
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.css']
})
export class ExplorerComponent implements OnInit {

availableTools: ToolDto[] = [];
toolToShow: ToolDto = {name: "", description: "", status: ""};
showToolDetailCard: boolean = false;

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

  toggleToolDetails(tool: ToolDto): void {
      this.showToolDetailCard = !this.showToolDetailCard;
      if (this.showToolDetailCard) {
          this.toolToShow = tool;
      } else {
          this.toolToShow = {name: "", description: "", status: ""};
     }
  }
}
