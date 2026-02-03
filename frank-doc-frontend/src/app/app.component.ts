import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgFFDoc } from '@frankframework/doc-library-ng';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { AlertComponent } from '@frankframework/angular-components';
import { FeedbackComponent } from './components/feedback/feedback.component';
import { AppService } from './app.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [HeaderComponent, FooterComponent, RouterOutlet, AlertComponent, FeedbackComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  protected error: string | null = null;
  protected readonly appService: AppService = inject(AppService);

  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.ffDoc.initialize(environment.frankDocUrl);
  }
}
