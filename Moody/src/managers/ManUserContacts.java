/**
 * 
 */
package managers;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import model.ModConstants;
import restPackage.MoodleContact;
import restPackage.MoodleContactAction;
import restPackage.MoodleContactState;
import restPackage.MoodleMessage;
import restPackage.MoodleServices;
import android.content.Context;
import connections.DataAsyncTask;

/**
 * @author MoodyProject Team
 * 
 * @contributor Sfbramos
 * 
 */
public class ManUserContacts {
	private ManSession session;
	private Object getContext;
	private Context context;
	private ManDataStore data;
	private String userId;
	private String url;
	private String token;

	public ManUserContacts(Context context) {
		this.context = context;
		this.data = new ManDataStore(context);
		this.session = new ManSession(context);
		this.userId = session.getValues(ModConstants.KEY_ID, null);
		this.url = session.getValues(ModConstants.KEY_URL, null);
		this.token = session.getValues(ModConstants.KEY_TOKEN, null);
	}

	/**
	 * 
	 * Method that gets the contacts of the user
	 * 
	 * 
	 * @param resources
	 * @param context
	 * 
	 * @return all of the contacts of the user.
	 * @return MoodleContact[]
	 */
	public MoodleContact[] getContacts() {
		String fileName = MoodleServices.CORE_MESSAGE_GET_CONTACTS.name()
				+ userId;

		if (!data.isInCache(fileName))
			setContacts();

		return (MoodleContact[]) data.getData(fileName);
	}

	public MoodleContact getContact(Long id) {
		MoodleContact[] contacts = getContacts();

		for (MoodleContact contact : contacts) {
			// do not remove the curve braces because it won't work.
			if (Long.valueOf(contact.getContactProfile().getId()) == Long
					.valueOf(id)) {
				return contact;
			}
		}

		return null;
	}

	public void setContacts() {
		String fileName = MoodleServices.CORE_MESSAGE_GET_CONTACTS.name()
				+ userId;

		try {
			getContext = new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_MESSAGE_GET_CONTACTS, null).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		data.storeData(getContext, fileName);
	}

	/**
	 * 
	 * Method to that perform actions on the contact provided an id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	private void actionContact(Long id, MoodleContactAction action) {
		Long[] a = { id };
		actionContacts(a, action);
		actionContacts(a, action);
	}

	/**
	 * + * + * Method that return true if the user has stranger contacts + * +
	 */
	public boolean hasStrangers() {
		MoodleContact[] contacts = getContacts();

		for (MoodleContact contact : contacts) {
			if (contact.getState() == MoodleContactState.STRANGERS) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Method that return true if the user has blocked contacts
	 */
	public boolean hasBlockedContacts() {
		return ((getBlockedContacts() != null) && (!getBlockedContacts()
				.isEmpty()));
	}

	/**
	 * 
	 * @param id
	 */
	public boolean isContact(Long id) {
		return ((getContact(id) != null));
	}

	/**
	 * 
	 * @param id
	 */
	public boolean isBlocked(Long id) {
		return (getBlockedContact(id) != null);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean isStranger(Long id) {
		return (isContact(id) && (getContact(id).getState() == MoodleContactState.STRANGERS));
	}

	/**
	 * 
	 * Method to that perform actions on the contact provided an a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	private void actionContacts(Long[] ids, MoodleContactAction action) {

		MoodleServices service = null;

		try {
			switch (action) {
			case BLOCK:
				service = MoodleServices.CORE_MESSAGE_BLOCK_CONTACTS;
				break;
			case CREATE:
				service = MoodleServices.CORE_MESSAGE_CREATE_CONTACTS;
				break;
			case DELETE:
				service = MoodleServices.CORE_MESSAGE_DELETE_CONTACTS;
				break;
			case UNBLOCK:
				service = MoodleServices.CORE_MESSAGE_UNBLOCK_CONTACTS;
				break;
			}

			new DataAsyncTask().execute(url, token, service, ids).get();

			setContacts();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method to that send the message to the contact.
	 * 
	 * @param message
	 * @param id
	 * @param action
	 */
	public void sendMessage(String message, Long id) {
		try {

			new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_MESSAGE_SEND_INSTANT_MESSAGES,
					new MoodleMessage(id, message, "4")).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method that deletes the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void deleteContact(Long id) {
		if (isBlocked(id)) {
			ArrayList<MoodleContact> blockeds = getBlockedContacts();

			blockeds.remove(getContact(id));
			saveBlockedContacts(blockeds);
			actionContact(id, MoodleContactAction.UNBLOCK);
		}

		if (isContact(id))
			actionContact(id, MoodleContactAction.DELETE);
	}

	/**
	 * 
	 * Method that deletes the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 *            TODO: adapt to delete blocked contacts
	 */
	public void deleteContacts(Long[] ids) {
		actionContacts(ids, MoodleContactAction.DELETE);
	}

	/**
	 * 
	 * Method that creates the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void createContact(Long id) {
		if ((!isContact(id)) || (isStranger(id)))
			actionContact(id, MoodleContactAction.CREATE);
	}

	/**
	 * 
	 * Method that creates the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	public void createContacts(Long[] ids) {
		actionContacts(ids, MoodleContactAction.CREATE);
	}

	/**
	 * 
	 * Method that blocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void blockContact(Long id) {
		if (!isBlocked(id)) {
			ArrayList<MoodleContact> blockeds = getBlockedContacts();

			if (blockeds == null)
				blockeds = new ArrayList<MoodleContact>();

			blockeds.add(getContact(id));

			saveBlockedContacts(blockeds);
			actionContact(id, MoodleContactAction.BLOCK);
		}
	}

	/**
	 * 
	 * Method that blocks the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 *            TODO: adapt to block many contacts
	 */
	public void blockContacts(Long[] ids) {
		actionContacts(ids, MoodleContactAction.BLOCK);
	}

	/**
	 * 
	 * Method that ublocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void unblockContact(Long id) {
		if (isBlocked(id)) {
			ArrayList<MoodleContact> blockeds = getBlockedContacts();
			MoodleContact unblocked = null;

			// don't change beacause won't work.
			for (MoodleContact contact : blockeds) {
				if (Long.valueOf(contact.getContactProfile().getId()) == Long
						.valueOf(id)) {
					unblocked = contact;
				}
			}

			blockeds.remove(unblocked);
			saveBlockedContacts(blockeds);

			actionContact(id, MoodleContactAction.UNBLOCK);

			if (unblocked.getState() != MoodleContactState.STRANGERS)
				createContact(id);
		}
	}

	/**
	 * 
	 * Method that unblocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 *            TODO: adapt to unblock many contacts
	 */
	public void unblockContacts(Long[] ids) {
		actionContacts(ids, MoodleContactAction.UNBLOCK);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public ArrayList<MoodleContact> getBlockedContacts() {
		try {
			return (ArrayList<MoodleContact>) data.getData("Blocked");
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public MoodleContact getBlockedContact(Long id) {
		ArrayList<MoodleContact> list = getBlockedContacts();

		try {
			if ((list == null) || list.isEmpty())
				return null;

			for (MoodleContact contact : list) {
				if (Long.valueOf(contact.getContactProfile().getId()) == Long
						.valueOf(id)) {
					return contact;
				}
			}
		} catch (Exception ex) {

		}

		return null;
	}

	/**
	 * 
	 * @param context
	 * @param blocked
	 */
	public void saveBlockedContacts(ArrayList<MoodleContact> blocked) {
		data.storeData(blocked, "Blocked");
	}
}