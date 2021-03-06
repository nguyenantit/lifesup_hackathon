package fi.lifesup.hackathon.web.rest;

import com.codahale.metrics.annotation.Timed;
import fi.lifesup.hackathon.domain.Challenge;

import fi.lifesup.hackathon.repository.ChallengeRepository;
import fi.lifesup.hackathon.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Challenge.
 */
@RestController
@RequestMapping("/api")
public class ChallengeResource {

    private final Logger log = LoggerFactory.getLogger(ChallengeResource.class);
        
    @Inject
    private ChallengeRepository challengeRepository;

    /**
     * POST  /challenges : Create a new challenge.
     *
     * @param challenge the challenge to create
     * @return the ResponseEntity with status 201 (Created) and with body the new challenge, or with status 400 (Bad Request) if the challenge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/challenges")
    @Timed
    public ResponseEntity<Challenge> createChallenge(@Valid @RequestBody Challenge challenge) throws URISyntaxException {
        log.debug("REST request to save Challenge : {}", challenge);
        if (challenge.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("challenge", "idexists", "A new challenge cannot already have an ID")).body(null);
        }
        Challenge result = challengeRepository.save(challenge);
        return ResponseEntity.created(new URI("/api/challenges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("challenge", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /challenges : Updates an existing challenge.
     *
     * @param challenge the challenge to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated challenge,
     * or with status 400 (Bad Request) if the challenge is not valid,
     * or with status 500 (Internal Server Error) if the challenge couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/challenges")
    @Timed
    public ResponseEntity<Challenge> updateChallenge(@Valid @RequestBody Challenge challenge) throws URISyntaxException {
        log.debug("REST request to update Challenge : {}", challenge);
        if (challenge.getId() == null) {
            return createChallenge(challenge);
        }
        Challenge result = challengeRepository.save(challenge);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("challenge", challenge.getId().toString()))
            .body(result);
    }

    /**
     * GET  /challenges : get all the challenges.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of challenges in body
     */
    @GetMapping("/challenges")
    @Timed
    public List<Challenge> getAllChallenges() {
        log.debug("REST request to get all Challenges");
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges;
    }

    /**
     * GET  /challenges/:id : get the "id" challenge.
     *
     * @param id the id of the challenge to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the challenge, or with status 404 (Not Found)
     */
    @GetMapping("/challenges/{id}")
    @Timed
    public ResponseEntity<Challenge> getChallenge(@PathVariable Long id) {
        log.debug("REST request to get Challenge : {}", id);
        Challenge challenge = challengeRepository.findOne(id);
        return Optional.ofNullable(challenge)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /challenges/:id : delete the "id" challenge.
     *
     * @param id the id of the challenge to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/challenges/{id}")
    @Timed
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        log.debug("REST request to delete Challenge : {}", id);
        challengeRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("challenge", id.toString())).build();
    }

}
