package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {
	
	Session session;

	public ParticipantService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Participant> getAll() {
		return session.createCriteria(Participant.class).list(); // wylistowanie wszystkich obiektow
																				// klasy Participant
	}

	public Participant findByLogin(String login) {
		Participant participant = (Participant) session.get(Participant.class, login); // (Participant) -
																										// rzutowanie na
																										// Participant
		return participant;

	}

	public void add(Participant participant) {
		Transaction transaction = this.session.beginTransaction();
		session.save(participant);
		transaction.commit();

	}

	public void delete(Participant participant) {
		Transaction transaction = this.session.beginTransaction();
		session.delete(participant);
		transaction.commit();

	}

	public void update(Participant participant) {
		Transaction transaction = this.session.beginTransaction();
		session.merge(participant);
		transaction.commit();

	}
	
}
