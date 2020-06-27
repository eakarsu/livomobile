//
//  ErrorHelper.swift
//  Livo
//
//  Created by Deniz Acay on 28/11/15.
//  Copyright © 2015 Livo Mobile. All rights reserved.
//

import UIKit
import MessageUI

import SSZipArchive

class ErrorHelper: NSObject, MFMailComposeViewControllerDelegate {
    
    var completeBlock : (() -> Void)?
    
    static func reportErrorsViaMail(currentVC: UIViewController) {
        
        // Check e-mail capability.
        if !MFMailComposeViewController.canSendMail() {
            
            log.error("Device is not configured to send e-mail.")
        }
        
        // Zip log files.
        let zipFile = zipLogFiles()
        
        // Check if zipping was successful.
        if zipFile == nil {
            
            log.error("Failed to zip log files.")
            
            //TODO Try an alternative method
            
            return
        }
        
        // Create a mail compose view controller.
        let mailComposeVC = MFMailComposeViewController()
        
        // Get application ID.
        let applicationId = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        // Get support mail address.
        let supportMailAddress = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Support.MailAddress)
        
        // Configure mail compose view controller.
        mailComposeVC.setSubject("\(applicationId!) - Application Logs")
        mailComposeVC.setMessageBody("", isHTML: false)
        mailComposeVC.setToRecipients([supportMailAddress!])
        
        // Load data from the zip file.
        let attachmentData = NSData(contentsOfURL: zipFile!)
        
        // Add attachment.
        mailComposeVC.addAttachmentData(attachmentData!, mimeType: "application/zip", fileName: "\(applicationId!)-logs.zip")
        
        // Create class instance.
        let delegate = ErrorHelper()
        
        // Set compose view controller delegate.
        mailComposeVC.mailComposeDelegate = delegate
        
        // Set completion block.
        delegate.completeBlock = {

            try? NSFileManager.defaultManager().removeItemAtURL(zipFile!)
        }
        
        // Present the view controller.
        currentVC.presentViewController(mailComposeVC, animated: true, completion: nil)
    }
    
    private static func zipLogFiles() -> NSURL? {
        
        // Get the logs directory.
        let logsDir = logsDirectory()
        
        // CHeck logs directory.
        if logsDir == nil {
            
            return nil
        }
        
        // Calculate a temporary zip file destination.
        let tempZipFile = applicationTemporaryDirectory().URLByAppendingPathComponent("livo-logs.zip", isDirectory: false)
        
        // Zip the log files.
        if SSZipArchive.createZipFileAtPath(tempZipFile.absoluteURL.path, withContentsOfDirectory: logsDir!.absoluteURL.path) {
            
            return tempZipFile
        }
        
        return nil
    }
    
    private static func logsDirectory() -> NSURL? {
        
        // Get application support directory.
        let appSupportDirectory = applicationSupportDirectory()
        
        if appSupportDirectory == nil {
            
            return nil
        }
        
        return appSupportDirectory?.URLByAppendingPathComponent("logs", isDirectory: true)
    }
    
    
    private static func applicationTemporaryDirectory() -> NSURL {
        
        // Get application temporary directory
        let applicationTemporaryDirectory = NSURL.fileURLWithPath(NSTemporaryDirectory(), isDirectory: true)
        
        log.debug("Using application temporary directory '\(applicationTemporaryDirectory)'...")
        
        // Return the directory
        return applicationTemporaryDirectory
    }
    
    private static func applicationSupportDirectory() -> NSURL? {
        
        // Get possible application support directories
        let appSupportDirectories = NSFileManager.defaultManager().URLsForDirectory(.ApplicationSupportDirectory, inDomains: .UserDomainMask)
        
        // Sanity check
        guard !appSupportDirectories.isEmpty else {
            
            log.error("No application support directory was found.")
            
            return nil
        }
        
        log.debug("Found \(appSupportDirectories.count) possible application support directories.")
        
        // Get the first application support directory
        let appSupportDirectory = appSupportDirectories[0].URLByAppendingPathComponent(NSBundle.mainBundle().bundleIdentifier!)
        
        log.debug("Using application support directory '\(appSupportDirectory.relativePath)'...")
        
        // Return the directory
        return appSupportDirectory
    }
    
    func mailComposeController(controller: MFMailComposeViewController, didFinishWithResult result: MFMailComposeResult, error: NSError?) {
        
        switch result {
            
        case MFMailComposeResultCancelled:
            
            log.error("Sending error report mail cancelled.")
            
        case MFMailComposeResultFailed:
            log.error("Failed to send error report mail.")
            if error != nil {
                log.error("\(error!)")
            }
            
        case MFMailComposeResultSaved:
            
            log.debug("Saved error report mail to drafts.")
            
        default:
            
            log.debug("Error report mail sent.")
        }
        
        // Dismiss mail compose view controller.
        controller.dismissViewControllerAnimated(true) { () -> Void in
            
            if self.completeBlock != nil {
                
                self.completeBlock!()
            }
        }
    }
}

