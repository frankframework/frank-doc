import { Pipe, PipeTransform } from '@angular/core';
import { splitOnPascalCaseRegex } from '../app.constants';

@Pipe({
  name: 'nameWbr',
  standalone: true,
})
export class NameWbrPipe implements PipeTransform {
  transform(value: string): string {
    return value.split(splitOnPascalCaseRegex).join('<wbr>');
  }
}
