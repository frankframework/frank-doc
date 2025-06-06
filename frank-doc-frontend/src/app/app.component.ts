import { Component, computed, inject, OnInit, Signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgFFDoc } from '@frankframework/ff-doc';
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
  protected version: Signal<string> = computed(() => {
    const ffDoc = this.ffDoc.ffDoc();
    if (ffDoc) this.appService.triggerApplicationLoaded();
    return ffDoc?.metadata.version ?? 'unknown';
  });
  protected error: string | null = null;

  private readonly appService: AppService = inject(AppService);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.ffDoc.initialize(environment.frankDocUrl);
  }
}
