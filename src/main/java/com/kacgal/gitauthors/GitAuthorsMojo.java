package com.kacgal.gitauthors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mojo(name = "git-authors", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class GitAuthorsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(property = "format", defaultValue = "${name} <${email}>")
    private String format;

    @Parameter(property = "joiner", defaultValue = ",")
    private String joiner;

    @Parameter(property = "sort-by", defaultValue = "FIRST_COMMIT")
    private Sort sort;

    @Parameter(property = "reverse", defaultValue = "false")
    private boolean reverse;

    public void execute() {
        try {
            Repository repo = new FileRepositoryBuilder().findGitDir().build();

            Map<PersonIdent, List<RevCommit>> authors = StreamSupport.stream(new Git(repo).log().call().spliterator(), false)
                    .collect(Collectors.groupingBy(RevCommit::getAuthorIdent));
            authors.values().forEach(commits -> commits.sort(Comparator.comparingInt(RevCommit::getCommitTime)));

            Comparator<PersonIdent> comparator;
            switch (sort) {
                case FIRST_COMMIT:
                    comparator = Comparator.comparingInt(author -> authors.get(author).get(0).getCommitTime());
                    break;
                case NUM_COMMITS:
                    comparator = Comparator.comparingInt(author -> authors.get(author).size());
                    break;
                default:
                    return;
            }
            if (reverse) {
                comparator = comparator.reversed();
            }
            String authorsString = authors.keySet().stream()
                    .sorted(comparator)
                    .map(author -> StrSubstitutor.replace(format, ImmutableMap.of("name", author.getName(), "email", author.getEmailAddress())))
                    .distinct()
                    .collect(Collectors.joining(joiner));

            project.getProperties().setProperty("git.authors", authorsString);

        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }
}