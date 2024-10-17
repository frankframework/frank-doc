import { Component, computed, OnInit, Signal } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { RouterOutlet } from '@angular/router';
import { AlertComponent } from '@frankframework/angular-components';
import { AppService } from './app.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, RouterOutlet, AlertComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  // eslint-disable-next-line unicorn/consistent-function-scoping
  protected version: Signal<string> = computed(() => this.appService.frankDoc()?.metadata.version ?? 'unknown');
  protected error: string | null = null;

  constructor(private appService: AppService) {}

  ngOnInit(): void {
    this.appService.getFrankDoc().subscribe({
      next: (data) => {
        this.appService.frankDoc.set(data);
      },
      error: (error: HttpErrorResponse) => {
        this.error = `Couldn't retrieve FrankDoc file: ${error.statusText}`;
      },
    });
  }
}
