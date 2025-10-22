import { UserDto } from './UserDto';

export interface ToolDto {
    name: string;
    picture?: Blob[];
    description: string;
    owner: UserDto;
    status: string;
}
