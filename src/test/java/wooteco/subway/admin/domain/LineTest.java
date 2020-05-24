package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {
	private Line line;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
		line.addLineStation(new LineStation(null, 1L, 10, 10));
		line.addLineStation(new LineStation(1L, 2L, 10, 10));
		line.addLineStation(new LineStation(2L, 3L, 10, 10));
	}

	@Test
	void addLineStation() {
		line.addLineStation(new LineStation(null, 4L, 10, 10));

		assertThat(line.getStations().getStations()).hasSize(4);
		LineStation lineStation = line.getStations().getStations().stream()
			.filter(it -> it.getPreStationId() == 4L)
			.findFirst()
			.orElseThrow(RuntimeException::new);
		assertThat(lineStation.getStationId()).isEqualTo(1L);
	}

	@Test
	void getLineStations() {
		List<Long> stationIds = line.getLineStationsId();

		assertThat(stationIds.size()).isEqualTo(3);
		assertThat(stationIds.get(0)).isEqualTo(1L);
		assertThat(stationIds.get(1)).isEqualTo(2L);
		assertThat(stationIds.get(2)).isEqualTo(3L);
	}

	@ParameterizedTest
	@ValueSource(longs = {1L, 2L, 3L})
	void removeLineStation(Long stationId) {
		line.removeLineStationById(stationId);

		assertThat(line.getStations().getStations()).hasSize(2);
	}

	@Test
	void findStationsFrom() {
		Station sampleStation1 = new Station(1L, "가역");
		Station sampleStation2 = new Station(2L, "나역");
		Station sampleStation3 = new Station(3L, "다역");
		Station sampleStation4 = new Station(4L, "라역");
		List<Station> stations = Arrays.asList(
			sampleStation1,
			sampleStation2,
			sampleStation3,
			sampleStation4
		);
		List<Station> matchingStations = line.findStationsFrom(stations);
		assertThat(matchingStations).containsExactly(sampleStation1, sampleStation2, sampleStation3);
	}
}
