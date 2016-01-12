package modules.database;

import modules.database.entities.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 02.12.15.
 */
public class JobController extends DatabaseController<Job, Object> {

    /**
     * persistiert den angegebenen job in der datenbank, zunächst müssen aber die übergebenen image und layoutconfig gespeichert werden, damit die verbindung
     * zum job abgelegt werden kann
     * zuseätzlich wir der übergebene user dem job als user zugeordnet
     * @param job abzuspeichender job
     * @param image abzuspeichendes bild
     * @param layoutConfig layout configuration für das bild
     * @param user benutzer, der den job angelegt hat
     */
    @Transactional
    public void persistJob(Job job, Image image, LayoutConfig layoutConfig, User user) {
        JPA.em().persist(image);
        JPA.em().persist(layoutConfig);

        job.setImage(image);
        job.setLayoutConfig(layoutConfig);
        job.setUser(selectEntity(User.class, "eMail", user.geteMail()));

        JPA.em().persist(job);
    }

    /**
     * liefert alle jobs eines Benutzers zurück
     * @param userName email des benutzers
     * @return liste der zum benutzer gehörigen jobs
     */
    public List<Job> getJobsFromUser(String userName) {
        User user = selectEntity(User.class, "eMail", userName);

        return selectEntityList(Job.class, user);
    }

    public Job getJobById(int id) throws Throwable {
        return JPA.withTransaction(() ->selectEntity(Job.class, "id", Integer.toString(id)));
    }

    /**
     * selektiert alle verfügbaren sprachen/länder und gibt diese zurück
     * @return alle verfügbar sprachen/länder
     * @throws Throwable
     */
    public List<Language> getAllCountryLanguages() throws Throwable {
        return JPA.withTransaction(() -> {
                    List<Language> rc = new ArrayList<>();
                    List<Country> countryList = selectEntityList(Country.class, null);

            rc.addAll(countryList.stream().map(country -> getCountryImpl(country.getCountry())).collect(Collectors.toList()));

            return rc;
                }
        );
    }

    /**
     * erstellt eine neue countryimpl um diese zurückzuliefern
     * @param country countryimpl die übernommen werden sollen
     * @return neu erstellte countryimpl
     */
    private Language getCountryImpl(CountryImpl country) {
        Language language = new Language();
        language.isoCode = country.getIsoCode();
        language.name = country.getName();
        return language;
    }

    /**
     * selektiert alle für jeden benutzer verfügbare layout configurationen
     * @return liste der configurationen
     */
    public List<LayoutConfig> getStandardTemplates() {
        return selectEntityList(LayoutConfig.class, null);
    }

    /**
     * persistiert einen job der keine layout configurationen für das übergebene bild besitzt
     * siehe persistJob(job, image, layoutConfig, user)
     * @param job zu speichernder job
     * @param image zu speicherndes bild
     * @param session email des benutzers
     */
    public void persistJob(Job job, Image image, String session) {
        JPA.withTransaction(() -> {
            JPA.em().persist(image);

            job.setImage(image);
            job.setUser(selectEntity(User.class, "eMail", session));
            job.setProcessed(false);

            JPA.em().persist(job);
        });
    }

    /**
     * selektiert alle nicht analysierten jobs, die die übergebene uploadid und dem benutzer mit der übergebenen email(username) haben
     * @param uploadID uploadid der jobs
     * @param username benutzer dem die jobs gehören
     * @return liste der selektierten jobs
     * @throws Throwable
     */
    public List<Job> getUnProcessedJobs(String uploadID, String username) throws Throwable {
        return JPA.withTransaction(() -> {
            ArrayList<String> whereColumn = new ArrayList<>();
            whereColumn.add("user");
            whereColumn.add("processed");
            whereColumn.add("uploadId");

            ArrayList<Object> whereValue = new ArrayList<>();
            whereValue.add(new UserController().selectUserFromMail(username));
            whereValue.add(false);
            whereValue.add(uploadID);

            return selectEntityList(Job.class, whereColumn, whereValue);
        });
    }

    /**
     * selektiert alle jobs mit der übergebenen uploadid
     * @param inUploadId upload id der jobs
     * @return liste der selektierten jobs
     * @throws Throwable
     */
    public List<Job> getJobsByUploadId(String inUploadId) throws Throwable {
        return JPA.withTransaction(() -> selectEntityList(Job.class, "uploadId", inUploadId));
    }

    private class Language{
        public String name;

        public String isoCode;
    }
}
