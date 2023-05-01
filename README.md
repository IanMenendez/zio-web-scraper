## About The Project

Web scraper written with Scala and ZIO capable of scraping tens of thousands of sites per minute

## Usage

Put a txt file in **src/main/resources/url.txt** with every URL you want to scrape by line.

By default the scraper scrapes each page every 1 hour.

To change that simply add a space after the url and then a number (by hour) specifying how frequently you want the site to be scraped.

The next line would scrape the github main page every 4 hours
```
    https://github.com/ 4
```


To run the program with your own transform pipeline and save the scraped pages you need to:

- write your transformation function that transforms the results from the scraper from a PageResult type to whatever you need for example extracting just parts of the html body
- write a ZSINK that will consume this values and do something for example saving the result in a database

Then change **Example.streamTransformerExample** to your transform function and **Example.sinkConsumerExample**: from the run function under Main

```
    def run = scrapersStream.flatMap(stream => StreamHandler.transformStreamResults(stream, nPar, Example.streamTransformerExample).run(Example.sinkConsumerExample)).provideLayer(defaultServices)

```

## TODO

- [ ] Retry scraping on certain cases
- [ ] Client request configuration to allow ip changing and proxies
- [ ] Improve error catching and recovery  
- [ ] Scrape JS urls  




