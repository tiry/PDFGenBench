
### Context

When doing a large scale benchmark of a document repository, it can be useful to generate attachment files.

In our context we want to generate PDF files:

 - that are all different
 - that contain valid text content
    - allowing to do meaningful full text search on them
 - containing text and images

The challenge is that generating 10B pdf files can take a very long time.

### Goal

The goal of this project is to experiement with various ways to generate PDF files and preview images.

**Libraries**

 - iText
    - currently tested
 - PDFBox
    - currently tested
 - Apache FOP
    - trying to stay away from XSL-FO!
 - JPedal 
    - commercial => not tested for now
 - jpod lib
    - dev seems to stalled ( unless https://github.com/scireum/jpod ?)
 - ImageMagik
    - could be a solution for thumbnail generation

**References**

 - http://abhishekkumararya.blogspot.com/2013/09/comparison-of-java-based-pdf-generation.html


### Example output

Here is the current tests results:

 - Corei7 (i7-8550U) with 4 Core + Hyperthreading (8 logical Cores)
 - Java 11 / Linux

NB: This is a laptop so IO probably sucks


    [INFO] Running TestPDF
    ----------------------------------------------------------
    Testing Template based generation using iText
      input file:statement_sample1.PDF

      Files: 25000 pdfs --- 892 docs/s
      IO   : 1339 MB  --- 47 MB/s

      Projected generation time for 10B files: 129 day(s) and 18 hour(s)
    ----------------------------------------------------------
    Testing Template based generation with Index pre-processing using iText
      input file:statement_sample1.PDF
      Files: 25000 pdfs --- 1923 docs/s
      IO   : 1339 MB  --- 103 MB/s

      Projected generation time for 10B files: 60 day(s) and 4 hour(s)
    ----------------------------------------------------------
    Testing Generate a new PDF document using iText
      input file:bank-logo.png
      Files: 25000 pdfs --- 961 docs/s
      IO   : 242 MB  --- 9 MB/s

      Projected generation time for 10B files: 120 day(s) and 10 hour(s)
    ----------------------------------------------------------
    Testing Template based generation with Index pre-processing using iText
      input file:nx-statement-small.pdf
      Files: 25000 pdfs --- 2083 docs/s
      IO   : 887 MB  --- 73 MB/s

      Projected generation time for 10B files: 55 day(s) and 13 hour(s)
    ----------------------------------------------------------
    Testing Generate a new PDF Document using PDFBox
      input file:bank-logo.png
      Files: 25000 pdfs --- 312 docs/s
      IO   : 272 MB  --- 3 MB/s

      Projected generation time for 10B files: 370 day(s) and 23 hour(s)
    ----------------------------------------------------------
    Testing Update an existing PDF template using PDFBox
      input file:statement_sample1.PDF
      Files: 25000 pdfs --- 1315 docs/s
      IO   : 1380 MB  --- 72 MB/s

      Projected generation time for 10B files: 88 day(s) and 0 hour(s)
    ----------------------------------------------------------
    Testing Update an existing PDF template using PDFBox and generate Thumbnails
      input file:statement_sample1.PDF
      Files: 25000 pdfs --- 156 docs/s
      IO   : 1667 MB  --- 10 MB/s

      Projected generation time for 10B files: 741 day(s) and 22 hour(s)
