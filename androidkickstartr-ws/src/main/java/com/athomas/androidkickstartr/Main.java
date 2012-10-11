package com.athomas.androidkickstartr;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;

import com.athomas.androidkickstartr.model.Application;
import com.athomas.androidkickstartr.model.State;
import com.athomas.androidkickstartr.util.StringUtils;

@Path("/")
public class Main {

	@POST
	@Produces("application/zip")
	public Response go(//

			// State
			@FormParam("actionBarSherlock") boolean actionBarSherlock,//
			@FormParam("navigationType") String navigationType,//
			@FormParam("androidAnnotations") boolean androidAnnotations,//
			@FormParam("restTemplate") boolean restTemplate,//
			@FormParam("maven") boolean maven,//
			@FormParam("nineOldAndroids") boolean nineOldAndroids,//
			@FormParam("supportV4") boolean supportV4,//
			@FormParam("acra") boolean acra,//
			@FormParam("eclipse") boolean eclipse,//

			// Application
			@FormParam("packageName") String packageName,//
			@FormParam("name") String name,//
			@FormParam("activity") String activity,//
			@FormParam("activityLayout") String activityLayout//
	) {

		boolean listNavigation = false;
		boolean tabNavigation = false;

		if (navigationType != null) {
			tabNavigation = navigationType.equals("tabNavigation");
			listNavigation = navigationType.equals("listNavigation");
		}

		if (StringUtils.isEmpty(packageName)) {
			packageName = "com.androidkickstarter.app";
		}
		if (StringUtils.isEmpty(name)) {
			name = "MyApplication";
		}
		if (StringUtils.isEmpty(activity)) {
			activity = "MainActivity";
		}
		if (StringUtils.isEmpty(activityLayout)) {
			activityLayout = "activity_main";
		}

		State state = new State.Builder().//
				actionBarSherlock(actionBarSherlock).//
				listNavigation(listNavigation).//
				tabNavigation(tabNavigation).//
				viewPager(false).//
				viewPagerIndicator(false).//
				androidAnnotations(androidAnnotations).//
				restTemplate(restTemplate). //
				maven(maven). //
				nineOldAndroids(nineOldAndroids). //
				supportV4(supportV4). //
				acra(acra). //
				eclipse(eclipse). //
				build();

		Application application = new Application.Builder().//
				packageName(packageName).//
				name(name).//
				activity(activity).//
				activityLayout(activityLayout).//
				minSdk(8).//
				targetSdk(16).//
				permissions(new ArrayList<String>()).//
				build();

		final Kickstartr kickstarter = new Kickstartr(state, application);
		final File file = kickstarter.start();

		if (file == null) {
			return Response.serverError().build();
		}

		StreamingOutput output = new StreamingOutput() {
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					FileUtils.copyFile(file, output);
					kickstarter.clean();
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};

		return Response //
				.ok(output) //
				.header("Content-Length", file.length()) //
				.header("Content-Disposition", "attachment; filename=" + file.getName()) //
				.build();
	}

}
