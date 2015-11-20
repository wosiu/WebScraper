rm -rf delabScraper delabScraper.zip
mkdir delabScraper delabScraper/target
cp run.sh delabScraper/
cp config.json delabScraper/
cp browse_empty.sh delabScraper/
cp target/scraper-1.0-SNAPSHOT-jar-with-dependencies.jar delabScraper/target/
zip -r delabScraper delabScraper/
rm -rf delabScraper
