import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { AppService } from '../app.service';
import { Elements } from '../app.types';
import { JavadocPipe } from './javadoc.pipe';

describe('JavadocPipe', () => {
  let appService: AppService;
  let domSanitizer: DomSanitizer;
  let pipe: JavadocPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AppService,
        {
          provide: DomSanitizer,
          useValue: {
            bypassSecurityTrustHtml: (html: string): string => html
          }
        }
      ]
    });
    appService = TestBed.inject(AppService);
    domSanitizer = TestBed.inject(DomSanitizer);
    pipe = new JavadocPipe(appService, domSanitizer);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transforms markdown link to html link', () => {
    expect(pipe.transform('A [link](http://example.com) to an example website', {}))
      .toEqual('A <a target="_blank" href="http://example.com" alt="link">link</a> to an example website');
  });

  it('transforms javadoc quotes to normal quotes', () => {
    expect(pipe.transform('A javadoc \\"quote\\"', {}))
      .toEqual('A javadoc "quote"');
  });

  it('transforms javadoc invalid link to html text', () => {
    expect(pipe.transform('A javadoc {@link http://example.com link} to an example website', {}))
      .toEqual('A javadoc link to an example website');
  });

  it('transforms basic javadoc valid link to html link', () => {
    const elements: Elements = {
      'nl.nn.adapterframework.pipes.Json2XmlValidator': {
        "name": "Json2XmlValidator",
        "fullName": "nl.nn.adapterframework.pipes.Json2XmlValidator",
        elementNames: []
      }
    }
    expect(pipe.transform('A javadoc {@link Json2XmlValidator}', elements))
      .toEqual('A javadoc <a href="/#/All/Json2XmlValidator">Json2XmlValidator</a>');
  });

  it('transforms javadoc link to class with label to html link', () => {
    const elements: Elements = {
      'nl.nn.adapterframework.pipes.PipeLineSession': {
        "name": "PipeLineSession",
        "fullName": "nl.nn.adapterframework.pipes.PipeLineSession",
        elementNames: []
      }
    }
    expect(pipe.transform('{@link PipeLineSession pipeLineSession}', elements))
      .toEqual('<a href="/#/All/PipeLineSession">pipeLineSession</a>');
  });

  it('transforms invalid javadoc link to class with method to html link', () => {
    const elements: Elements = {}
    expect(pipe.transform('{@link IPipe#configure()}', elements))
      .toEqual('IPipe.configure()');
  });

  it('transforms valid javadoc link to class with method to html link', () => {
    const elements: Elements = {
      'nl.nn.adapterframework.pipes.IPipe': {
        "name": "IPipe",
        "fullName": "nl.nn.adapterframework.pipes.IPipe",
        elementNames: []
      }
    }
    expect(pipe.transform('{@link IPipe#configure()}', elements))
      .toEqual('<a href="/#/All/IPipe">IPipe.configure()</a>');
  });

  it('transforms javadoc link to same class method to html link', () => {
    const elements: Elements = {}
    expect(pipe.transform('{@link #doPipe(Message, PipeLineSession) doPipe}', elements))
      .toEqual('doPipe');
  });

  it('transforms javadoc links without parentheses', () => {
    const elements: Elements = {}
    expect(pipe.transform('{@link #setDestinationType destinationType} = {@link DestinationType#TOPIC TOPIC}', elements))
      .toEqual('destinationType = TOPIC');
  });
});
