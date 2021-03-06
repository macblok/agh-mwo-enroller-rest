package com.company.enroller.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;
import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@Autowired
	ParticipantService participantService;

	// GET localhost:8080/meetings
	// GET localhost:8080/meetings?sort=asc
	// GET localhost:8080/meetings?title=relax+after+job
	// GET http://localhost:8080/meetings?user=user5
	//localhost:8080/meetings?sort=asc&login=user3&title=relax
	@RequestMapping(value = "", method = RequestMethod.GET)

	public ResponseEntity<?> getMeetings(@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "login", required = false) String login) {
		Collection<Meeting> meetings = meetingService.getAll();

		if (title != null) {
			Collection<Meeting> meetingsWithTitle = meetingService.findByTitleOrDescription(title);

			if (meetingsWithTitle.isEmpty()) {
				return new ResponseEntity("There are no meetings with title or description containing " + title,
						HttpStatus.NOT_FOUND);
			} else {
				meetings.retainAll(meetingsWithTitle);
			}
		}

		if (login != null) {
			Collection<Meeting> meetingsWithLogin = meetingService.findMeetingsByParticipant(login);
			if (meetingsWithLogin.isEmpty()) {
				return new ResponseEntity("User " + login + " is not registered for any meeting", HttpStatus.NOT_FOUND);
			} else {
				meetings.retainAll(meetingsWithLogin);
			}
		}
		
		if (sort != null && sort.equals("asc")) {
			meetingService.ascendingSortByTitles(meetings);
		}

		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	// GET localhost:8080/meetings/2
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)

	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("There is no meeting with id " + id, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	// POST http://localhost:8080/meetings
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
		if (meetingService.findById(meeting.getId()) != null) {
			return new ResponseEntity<String>(
					"Unable to create. Meeting with ID " + meeting.getId() + " already exists", HttpStatus.CONFLICT);

		}

		meetingService.create(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);

	}

	// DELETE http://localhost:8080/meetings/4
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("Unable to delete. Meeting with given id doesn't exist.", HttpStatus.NOT_FOUND);
		}
		meetingService.delete(meeting);
		return new ResponseEntity<Meeting>(HttpStatus.NO_CONTENT);
	}

	// PUT http://localhost:8080/meetings/3
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)

	public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
		Meeting foundMeeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		foundMeeting.setTitle(meeting.getTitle());
		foundMeeting.setDescription(meeting.getDescription());
		foundMeeting.setDate(meeting.getDate());
		meetingService.update(foundMeeting);

		return new ResponseEntity<Meeting>(foundMeeting, HttpStatus.OK);
	}

	// GET http://localhost:8080/meetings/3/participants
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<String>("There is no meeting with id " + id, HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meeting.getParticipants();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	// POST http://localhost:8080/meetings/3/participants
	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)

	public ResponseEntity<?> addMeetingParticipant(@PathVariable("id") long id, @RequestBody Participant participant) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("There is no meeting with id " + id, HttpStatus.NOT_FOUND);
		}

		if (participantService.findByLogin(participant.getLogin()) == null) {
			return new ResponseEntity("There is no participant with login " + participant.getLogin(),
					HttpStatus.NOT_FOUND);
		}

		meeting.addParticipant(participant);
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);

	}

	// DELETE http://localhost:8080/meetings/3/participants/user5
	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)

	public ResponseEntity<?> removeMeetingParticipant(@PathVariable("id") long id,
			@PathVariable("login") String login) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity("There is no meeting with id " + id, HttpStatus.NOT_FOUND);
		}

		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity("There is no participant with login " + login + " in the meeting with id " + id,
					HttpStatus.NOT_FOUND);
		}

		meeting.removeParticipant(participant);
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);

	}

}
