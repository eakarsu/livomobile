//
//  WarningViewController.swift
//  Livo
//
//  Created by omur on 26/07/15.
//  Copyright (c) 2015 Livo Mobile. All rights reserved.
//

import UIKit
import SSKeychain

class SettingsHelpViewController: UIViewController {
    
    @IBOutlet private weak var imageView : UIImageView?;
    @IBOutlet private weak var textView : UITextView?;
    
    private var authenticationSettings : NSMutableDictionary?
    private var authenticationToken : AuthenticationToken?
    
    private var notifCount = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Load the launch image
        let launchImage = UIImage(named: "LaunchImage-700-568h");
        
        // Set the image view's image to the launch image
        self.imageView?.image = launchImage;
        
        // Check settings initially
        self.checkSettings()
        
        // Register observer for the settings change event
        NSNotificationCenter.defaultCenter().addObserver(self, selector: #selector(SettingsHelpViewController.settingsChanged(_:)), name: NSUserDefaultsDidChangeNotification, object: nil)
    }
    
    override func shouldAutorotate() -> Bool {
        
        return false
    }
    
    override func supportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        
        return .Portrait
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
    // MARK: - Notification handling
    func settingsChanged(notification: NSNotification) {
        
        notifCount += 1
        print("Notification received ---> " + String(notifCount))
        
        if notifCount == 1 {
            self.checkSettings()
        }
        
    }
    
    // MARK: - IBAction methods
    
    @IBAction func openSettings() {
        
        // Open application settings
        UIApplication.sharedApplication().openURL(NSURL(string: UIApplicationOpenSettingsURLString)!)
    }
    
    // MARK: - Helper methods
    
    private func checkSettings() {
        
        if !serverHostConfigurationValid() {
            
            // Set the message text
            self.textView?.text = NSLocalizedString("Please configure a valid server host to proceed.", comment: "")
            
        } else if !serverPortConfigurationValid() {
            
            // Set the message text
            self.textView?.text = NSLocalizedString("Please configure a valid server port to proceed.", comment: "")
            
        } else if !appIdConfigurationValid() {
            
            // Set the message text
            self.textView?.text = NSLocalizedString("Please configure a valid application Id to proceed.", comment: "")
            
        } else {
            
            // Set the message text
            self.textView?.text = TweaksHelper.tweakValue("Settings", collectionName: "Help Screen", name: "Settings Complete", defaultValue: NSLocalizedString("Settings are complete.", comment: ""), minimumValue: nil, maximumValue: nil)
            
            self.performAuthenticationSettings()

        }
    }
    
    private func serverHostConfigurationValid() -> Bool {
        
        let host = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Server.Host)
        
        log.debug("SettingsHelpViewController:: serverHostConfigurationValid --> '\(host)'...")
        return host != nil && !(host!.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet()).isEmpty)
    }
    
    private func serverPortConfigurationValid() -> Bool {
        
        let port = NSUserDefaults.standardUserDefaults().integerForKey(Settings.Server.Port)
        
        print("Port: " + String(port))
        
        return port > 0
    }
    
    
    private func appIdConfigurationValid() -> Bool {
        
        let applicationId : String? = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        log.debug("SettingsHelpViewController::appIdConfigurationValid --> '\(applicationId)'...")
        return applicationId != nil && !applicationId!.isEmpty && applicationId?.rangeOfString("default") == nil
    }
    
    private func arrangeServiceAddress() -> String {
        
        // Get the server settings
        let host = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Server.Host)!
        let port = NSUserDefaults.standardUserDefaults().integerForKey(Settings.Server.Port)
        
        // Construct the service address string
        let serviceAddress = "\(host):\(port)"
        
        return serviceAddress
    }
    
    private func performAuthenticationSettings() {
        
        // Get the server settings
        let applicationId : String? = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        log.debug("Authentication settings for '\(applicationId)' -- '\(applicationId?.rangeOfString("default"))'...")

        // Perform authentication
        ServiceHelper.authenticateSetting(applicationId!, success: { (map) in
            
            log.debug("Successfully received authentication settings for '\(applicationId)'.")
            
            self.authenticationSettings = map
            log.debug("map ---> '\(self.authenticationSettings)'.")
            
            self.showAppropriateViewController()
            
        }) { (message) -> Void in
            
            log.debug("To receive uthentication settings is failed for '\(applicationId)'.")
            
            self.handleError(message!, nextViewControllerIdentifier:"SettingsHelpViewController")
        }
        
    }
    
    func showAppropriateViewController() -> Void {
            
        let mainStoryboard = UIStoryboard(name: "Main", bundle: nil);
        
        var targetViewController : UIViewController = UIViewController()
        
        let authKey = self.authenticationSettings != nil ? self.authenticationSettings!.allKeys[0] as? String : nil
        log.debug("checkSettings ---> '\(authKey)'")
        
        if(authKey != nil){
            
            let process = authKey!.componentsSeparatedByString(".")[1];
            log.debug("checkSettings::process ---> '\(process)' -- \(process.rangeOfString("company"))")
            
            if(process == "any"){
                
                // Construct the service address string
                let serviceAddress = self.arrangeServiceAddress()
                
                // Clear previous values
                self.clearKeychainAccounts("CA:\(serviceAddress)")
                self.clearKeychainAccounts("UA:\(serviceAddress)")
                
                // Save the company ID and secret
                SSKeychain.setPassword("LivoAny", forService: "CA:\(serviceAddress)", account: "LivoAny")
                // Save the user principal and credentials
                SSKeychain.setPassword("LivoAnY", forService: "UA:\(serviceAddress)", account: "livoany")
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("SilentLoginViewController") as! SilentLoginViewController
                
            }
            else
            if(process == "company"){
                
                // Construct the service address string
                let serviceAddress = self.arrangeServiceAddress()
                
                // Clear previous values
                self.clearKeychainAccounts("UA:\(serviceAddress)")
                
                // Save the user principal and credentials
                SSKeychain.setPassword("LivoComP", forService: "UA:\(serviceAddress)", account: "livocomp")
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("CompanyLoginViewController") as! CompanyLoginViewController
                
            }
            else
            if(process == "System" || process == "ldap"){
                
                self.performCompanyAuthentication("LivoUsers", companySecret: "LivoUsers")
                
                let nextViewController : UserLoginViewController = mainStoryboard.instantiateViewControllerWithIdentifier("UserLoginViewController") as! UserLoginViewController
                nextViewController.authenticationToken = self.authenticationToken
                
                targetViewController = nextViewController
                
            }
            else{
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("CompanyLoginViewController") as! CompanyLoginViewController
                    
            }
            
        }
        
        // Show the next view controller
        NSOperationQueue.mainQueue().addOperationWithBlock { () -> Void in
            
            self.navigationController?.pushViewController(targetViewController, animated: true)
            
        }
        
    }
    
    private func handleError(message: String, nextViewControllerIdentifier: String) {
        
        NSOperationQueue.mainQueue().addOperationWithBlock({ () -> Void in
            
            let alert = UIAlertController(title: NSLocalizedString("Authentication Failed", comment: ""), message: message, preferredStyle: .Alert)
            
            let alertAction = UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .Default, handler: { (action : UIAlertAction!) -> Void in
                
                alert.dismissViewControllerAnimated(true, completion: nil)
                
                // Instantiate the next view controller
                let nextViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewControllerWithIdentifier(nextViewControllerIdentifier)
                
                // Present the view controller
                self.navigationController!.pushViewController(nextViewController, animated: true)
            })
            
            alert.addAction(alertAction)
            alert.view.tintColor = UIColor.grayColor()
            
            self.presentViewController(alert, animated: true, completion: {
                
                self.navigationController!.popViewControllerAnimated(true)
            })
        })
    }
    
    private func clearKeychainAccounts(serviceName: String) {
        
        // Get accounts for service
        let accounts = SSKeychain.accountsForService(serviceName) ?? NSArray()
        
        // Iterate through the accounts
        for account in accounts {
            
            // Get the first account available
            let accountDict = account as! NSDictionary
            
            // Get the account string from the account
            let accountStr = accountDict[NSString(format: kSecAttrAccount)] as! String
            
            // Delete account data
            SSKeychain.deletePasswordForService(serviceName, account: accountStr)
        }
    }
    
    private func performCompanyAuthentication(companyId: String, companySecret: String) {
        
        log.debug("performCompanyAuthentication: company '\(companyId)'...")
        
        // Get the configured application ID
        let applicationId = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        // Perform authentication
        ServiceHelper.authenticateCompany(companyId, companySecret: companySecret, appName: applicationId!, success: { (token) -> Void in
            
            log.debug("Successfully completed company authentication with ID '\(companyId)'.")
            
            self.authenticationToken = token
            
        }) { (message) -> Void in
            
            log.debug("Company authentication failed for ID '\(companyId)'.")
            
            self.handleError(message!, nextViewControllerIdentifier:"CompanyLoginViewController")
        }
    }
}
