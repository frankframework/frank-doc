import { Pipe, PipeTransform } from '@angular/core';
import { splitOnPascalCaseRegex } from '../app.constants';

@Pipe({
  name: 'nameWbr',
  standalone: true,
})
export class NameWbrPipe implements PipeTransform {
  transform(value: string, breakpoint?: RegExp | string): string {
    if (breakpoint) return value.split(breakpoint).join(`<wbr>${breakpoint}`);
    return value.split(splitOnPascalCaseRegex).join('<wbr>');
  }
}
