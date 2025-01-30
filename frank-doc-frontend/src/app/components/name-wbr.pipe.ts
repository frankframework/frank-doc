import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'nameWbr',
  standalone: true,
})
export class NameWbrPipe implements PipeTransform {
  transform(value: string): string {
    return value.split(/(?=[A-Z])/).join('<wbr>');
  }
}
