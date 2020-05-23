package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineDetailResponse;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.WholeSubwayResponse;
import wooteco.subway.admin.service.LineService;

@RequestMapping("/lines")
@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest request) {
		Line persistLine = lineService.save(request.toLine());

		return ResponseEntity
			.created(URI.create("/lines/" + persistLine.getId()))
			.body(LineResponse.of(persistLine));
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> showLine() {
		return ResponseEntity.ok().body(LineResponse.listOf(lineService.showLines()));
	}

	@GetMapping("/detail")
	public ResponseEntity<WholeSubwayResponse> wholeLines() {
		return ResponseEntity.ok()
			.body(lineService.wholeLines());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineDetailResponse> retrieveLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(lineService.findLineWithStationsById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id, @Valid @RequestBody LineRequest request) {
		lineService.updateLine(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/stations")
	public ResponseEntity<Void> addLineStation(@PathVariable Long lineId,
		@RequestBody LineStationCreateRequest request) {
		lineService.addLineStation(lineId, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity<Void> removeLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.noContent().build();
	}
}
