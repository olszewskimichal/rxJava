package pl.michal.olszewski.reactive.wjug.nurkiewicz;

import static io.reactivex.Flowable.generate;

import io.reactivex.Emitter;
import io.reactivex.Flowable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.stream.Stream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.jupiter.api.Test;

public class LoadingFile {

  @Test
  void loadingFileWithJava8Style() throws IOException {
    final String filePath = "foobar.txt";
    try (final BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      reader.lines()
          .filter(line -> !line.startsWith("#"))
          .map(String::toLowerCase)
          .flatMap(line -> Stream.of(line.split(" ")))
          .forEach(System.out::println);
    }
  }

  @Test
  void readFileWithFlowable() {
    final String filePath = "foobar.txt";
    final Flowable<String> file = generate(
        () -> new BufferedReader(new FileReader(filePath)),
        (reader, emitter) -> {
          final String line = reader.readLine();
          if (line != null) {
            emitter.onNext(line);
          } else {
            emitter.onComplete();
          }
        },
        BufferedReader::close
    );
    file.subscribe(System.out::println);
  }

  @Test
  void readFIleWithFlowable() {
    final String filePath = "foobar.txt";
    final Flowable<String> using = Flowable.using(
        () -> new BufferedReader(new FileReader(filePath)),
        reader -> Flowable.fromIterable(() -> reader.lines().iterator()),
        BufferedReader::close
    );
    using.subscribe(System.out::println);
  }

  @Test
  void readBigXmlFile() {
    final Flowable<Trackpoint> trackpoints = generate(
        () -> staxReader("track.gpx"),
        this::pushNextTrackpoint,
        XMLStreamReader::close);
    trackpoints.subscribe(v -> System.out.println(v.getLat() + " " + v.getLon()));
  }

  XMLStreamReader staxReader(final String name) throws XMLStreamException, FileNotFoundException {
    final InputStream inputStream = new BufferedInputStream(new FileInputStream(name));
    return XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
  }

  void pushNextTrackpoint(final XMLStreamReader reader, final Emitter<Trackpoint> emitter) throws XMLStreamException {
    final Trackpoint trkpt = nextTrackpoint(reader);
    if (trkpt != null) {
      emitter.onNext(trkpt);
    } else {
      emitter.onComplete();
    }
  }

  Trackpoint nextTrackpoint(final XMLStreamReader r) throws XMLStreamException {
    while (r.hasNext()) {
      final int event = r.next();
      switch (event) {
        case XMLStreamConstants.START_ELEMENT:
          if (r.getLocalName().equals("trkpt")) {
            return parseTrackpoint(r);
          }
          break;
        case XMLStreamConstants.END_ELEMENT:
          if (r.getLocalName().equals("gpx")) {
            return null;
          }
          break;
      }
    }
    return null;
  }

  Trackpoint parseTrackpoint(final XMLStreamReader r) {
    return new Trackpoint(
        new BigDecimal(r.getAttributeValue("", "lat")),
        new BigDecimal(r.getAttributeValue("", "lon"))
    );
  }
}

class Trackpoint {

  private final BigDecimal lat;
  private final BigDecimal lon;

  Trackpoint(final BigDecimal lat, final BigDecimal lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public BigDecimal getLon() {
    return lon;
  }
}

