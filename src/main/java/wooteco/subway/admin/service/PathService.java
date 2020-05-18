package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Path;
import wooteco.subway.admin.domain.PathSearchType;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.PathRequest;
import wooteco.subway.admin.dto.ShortestPathResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.NoSuchSourceStationException;
import wooteco.subway.admin.exception.NoSuchTargetStationException;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@Transactional
@Service
public class PathService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public PathService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	@Transactional(readOnly = true)
	public ShortestPathResponse getShortestPath(PathRequest request) {
		Station sourceStation = stationRepository.findByName(request.getSource()).orElseThrow(NoSuchSourceStationException::new);
		Station targetStation = stationRepository.findByName(request.getTarget()).orElseThrow(NoSuchTargetStationException::new);

		Long targetStationId = targetStation.getId();
		Long sourceStationId = sourceStation.getId();
		PathSearchType type = request.getType();
		Path graphLines = new Path(lineRepository.findAll());
		List<Long> shortestPath = graphLines.findShortestPath(sourceStationId, targetStationId, type);
		List<StationResponse> stationResponses = StationResponse.listOf(findStationsByIds(shortestPath));
		int distance = 0;
		int duration = 0;

		if (type == PathSearchType.DISTANCE) {
			distance = graphLines.calculateShortestDistance(sourceStationId, targetStationId);
			duration = graphLines.calculateDurationForShortestDistancePath(sourceStationId, targetStationId);
		}
		if (type == PathSearchType.DURATION) {
			duration = graphLines.calculateShortestDuration(sourceStationId, targetStationId);
			distance = graphLines.calculateDistanceForShortestDurationPath(sourceStationId, targetStationId);
		}
		return new ShortestPathResponse(stationResponses, distance, duration);
	}

	private List<Station> findStationsByIds(List<Long> shortestPath) {
		return shortestPath.stream()
			.map(stationId -> stationRepository.findById(stationId).orElseThrow(IllegalAccessError::new))
			.collect(toList());
	}
}
