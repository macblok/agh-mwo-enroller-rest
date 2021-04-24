package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {
	
	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		return connector.getSession().createCriteria(Participant.class).list(); // wylistowanie wszystkich obiektow
																				// klasy Participant
	}

	public Participant findByLogin(String login) {
		Participant participant = (Participant) connector.getSession().get(Participant.class, login); // (Participant) -
																										// rzutowanie na
																										// Participant
		return participant;

	}

	public void add(Participant participant) {
		Transaction transaction = this.connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transaction.commit();

	}

	public void delete(Participant participant) {
		Transaction transaction = this.connector.getSession().beginTransaction();
		connector.getSession().delete(participant);
		transaction.commit();

	}

}
