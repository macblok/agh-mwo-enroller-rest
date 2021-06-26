package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = session.createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		Meeting meeting = (Meeting) session.get(Meeting.class, id);
		return meeting;
	}

	public Collection<Meeting> findByTitleOrDescription(String titleOrDescription) {
		String wanted = "'%" + titleOrDescription + "%'";
		String hql = "FROM Meeting meeting WHERE meeting.title LIKE " + wanted + " OR meeting.description LIKE "
				+ wanted;
		Query query = session.createQuery(hql);
		return query.list();

	}

	public void create(Meeting meeting) {
		Transaction transaction = this.session.beginTransaction();
		session.save(meeting);
		transaction.commit();

	}

	public void delete(Meeting meeting) {
		Transaction transaction = this.session.beginTransaction();
		session.delete(meeting);
		transaction.commit();

	}

	public void update(Meeting meeting) {
		Transaction transaction = this.session.beginTransaction();
		session.merge(meeting);
		transaction.commit();

	}

	public Collection<Meeting> findMeetingsByParticipant(String login) {
		String wanted = "'" + login + "'";
		String hql = "SELECT m FROM Meeting m INNER JOIN m.participants participants WHERE participants.login = " + wanted;
		Query query = session.createQuery(hql);
		query.toString();
		return query.list();

	}

}
