import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate',
  standalone: true,
})
export class TruncatePipe implements PipeTransform {
  transform(value: string | undefined, length: number): string | undefined {
    if (!(value && value.length > length)) return value;
    return `${value.slice(0, Math.max(0, length))}...`;
  }
}
